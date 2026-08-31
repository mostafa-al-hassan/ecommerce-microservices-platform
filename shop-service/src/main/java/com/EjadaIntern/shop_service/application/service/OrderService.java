package com.EjadaIntern.shop_service.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.shop_service.application.dto.OrderItemResponse;
import com.EjadaIntern.shop_service.application.dto.OrderResponse;
import com.EjadaIntern.shop_service.domain.dto.RefundRequest;
import com.EjadaIntern.shop_service.domain.dto.StockItemRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReleaseRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReservationRequest;
import com.EjadaIntern.shop_service.domain.dto.StockSaleRequest;
import com.EjadaIntern.shop_service.domain.dto.TransferRequest;
import com.EjadaIntern.shop_service.domain.dto.TransferResponse;
import com.EjadaIntern.shop_service.domain.model.Cart;
import com.EjadaIntern.shop_service.domain.model.CartItem;
import com.EjadaIntern.shop_service.domain.model.CartStatus;
import com.EjadaIntern.shop_service.domain.model.Order;
import com.EjadaIntern.shop_service.domain.model.OrderItem;
import com.EjadaIntern.shop_service.domain.model.OrderStatus;
import com.EjadaIntern.shop_service.domain.model.Payment;
import com.EjadaIntern.shop_service.domain.model.PaymentStatus;
import com.EjadaIntern.shop_service.domain.port.CartRepositoryPort;
import com.EjadaIntern.shop_service.domain.port.InventoryClientPort;
import com.EjadaIntern.shop_service.domain.port.OrderRepositoryPort;
import com.EjadaIntern.shop_service.domain.port.WalletClientPort;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepositoryPort orderRepo;
    private final CartRepositoryPort cartRepo;
    private final InventoryClientPort inventoryClient;
    private final WalletClientPort walletClient;

    @Transactional
    public OrderResponse createOrder(UUID userId) {
        // Validate active orders & cart
        List<Order> activeOrders = orderRepo.findActiveOrdersByUserId(userId);
        if (!activeOrders.isEmpty()) {
            throw new IllegalStateException(
                    "User has " + activeOrders.size() + " active order(s). Complete or cancel them first.");
        }

        Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("No active cart found for user"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty; add items before checkout");
        }

        try {
            inventoryClient.validateStock(
                    mapCartItemsToStockRequests(cart.getItems()));
        } catch (FeignException.BadRequest e) {
            throw new IllegalArgumentException(
                    "One or more items have a lower amount of stock. Please update your cart.", e);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "One or more products no longer exist in inventory.", e);
        } catch (FeignException.ServiceUnavailable | FeignException.GatewayTimeout e) {
            throw new IllegalStateException(
                    "Inventory service unavailable. Cannot validate stock. Please try again later.", e);
        }

        // Create PENDING order
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(calculateTotal(cart))
                .items(mapCartItemsToOrderItems(cart.getItems()))
                .orderNumber(orderNumber)
                .build();
        linkItemsToOrder(order);
        order = orderRepo.save(order);

        // add check if items are available before making payment

        try {
            Map<UUID, BigDecimal> sellerPayouts = reconstructSellerPayouts(order.getItems());

            Set<UUID> paidSellers = new HashSet<>();
            List<UUID> walletTxIds = new ArrayList<>(
                    order.getPayment() != null && order.getPayment().getWalletTransactionIds() != null
                            ? order.getPayment().getWalletTransactionIds()
                            : Collections.emptyList());

            for (Map.Entry<UUID, BigDecimal> entry : sellerPayouts.entrySet()) {
                UUID sellerId = entry.getKey();

                if (paidSellers.contains(sellerId)) {
                    continue;
                }

                try {
                    TransferResponse response = walletClient.transferFunds(
                            new TransferRequest(userId, sellerId, entry.getValue(), order.getId()));

                    walletTxIds.add(response.transactionId());
                    paidSellers.add(sellerId); // ← Mark as paid IMMEDIATELY after success

                } catch (FeignException.NotFound e) {
                    throw new EntityNotFoundException(
                            "Seller wallet not found: " + sellerId, e);
                } catch (FeignException.BadRequest e) {
                    throw new IllegalArgumentException(
                            "Payment rejected for seller " + sellerId + ": " + e.getMessage(), e);
                } catch (FeignException.ServiceUnavailable | FeignException.GatewayTimeout e) {
                    throw new IllegalStateException(
                            "Wallet service unavailable during seller payout. Order is in PENDING state; no stock reserved.",
                            e);
                }
            }

            // Reserve stock (only after ALL payments succeed)
            for (OrderItem item : order.getItems()) {
                try {
                    inventoryClient.reserveStock(
                            new StockReservationRequest(item.getProductId(), item.getQuantity(),
                                    order.getId().toString()));
                } catch (FeignException.NotFound e) {
                    throw new EntityNotFoundException(
                            "Product not found in inventory: " + item.getProductId(), e);
                } catch (FeignException.BadRequest e) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for product " + item.getProductId() + ": requested "
                                    + item.getQuantity(),
                            e);
                } catch (FeignException.ServiceUnavailable | FeignException.GatewayTimeout e) {
                    throw new IllegalStateException(
                            "Inventory service unavailable while reserving stock. Payment succeeded but stock reservation pending. Contact support with order ID: "
                                    + order.getId(),
                            e);
                }
            }

            // Confirm sale
            for (OrderItem item : order.getItems()) {
                try {
                    inventoryClient.confirmSale(
                            new StockSaleRequest(item.getProductId(), item.getQuantity(), order.getId().toString()));
                } catch (FeignException.NotFound e) {
                    throw new EntityNotFoundException(
                            "Reserved stock record not found for product " + item.getProductId()
                                    + ". Manual reconciliation required.",
                            e);
                } catch (FeignException.ServiceUnavailable | FeignException.GatewayTimeout e) {
                    throw new IllegalStateException(
                            "Inventory service unavailable while confirming sale. Payment succeeded but stock confirmation pending. Contact support with order ID: "
                                    + order.getId(),
                            e);
                }
            }

            // Finalize
            order.setStatus(OrderStatus.CONFIRMED);
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getTotalAmount())
                    .status(PaymentStatus.COMPLETED)
                    .walletTransactionIds(new ArrayList<>(walletTxIds))
                    .build();
            order.setPayment(payment);
            orderRepo.save(order);
            cartRepo.deleteById(cart.getId());

            return mapToResponse(order);

        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            compensateFailedOrder(order);
            throw e;
        }
    }

    public OrderResponse getOrderById(UUID orderId, UUID userId) {
        Order order = orderRepo.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));
        return mapToResponse(order);
    }

    public Page<OrderResponse> getOrdersByUserId(UUID userId, Pageable pageable) {
        return orderRepo.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Transactional
    public void cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepo.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));

        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel confirmed/completed order. Use refund instead.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            inventoryClient.releaseStock(
                    new StockReleaseRequest(item.getProductId(), item.getQuantity(), order.getId().toString()));
        }

        if (order.getStatus() == OrderStatus.PAYMENT_INITIATED && order.getPayment() != null) {
            Map<UUID, BigDecimal> sellerPayouts = reconstructSellerPayouts(order.getItems());
            for (UUID sellerId : sellerPayouts.keySet()) {
                walletClient.refundTransaction(
                        new RefundRequest(sellerId, order.getId().toString()));
            }
            order.getPayment().setStatus(PaymentStatus.FAILED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    @Transactional
    public void refundOrder(UUID orderId, UUID userId) {
        Order order = orderRepo.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED orders can be refunded.");
        }
        if (order.getPayment() == null || order.getPayment().getWalletTransactionIds().isEmpty()) {
            throw new IllegalStateException("No completed payment transactions found for this order.");
        }

        // refund money
        List<UUID> txIds = order.getPayment().getWalletTransactionIds();
        for (UUID txId : txIds) {
            walletClient.refundTransaction(
                    new RefundRequest(txId, order.getId().toString()));
        }

        order.setStatus(OrderStatus.REFUNDED);
        orderRepo.save(order);
    }

    // private helpers

    private void compensateFailedOrder(Order order) {
        log.error("Compensating failed order {}", order.getId());
        try {
            for (OrderItem item : order.getItems()) {
                inventoryClient.releaseStock(
                        new StockReleaseRequest(item.getProductId(), item.getQuantity(), order.getId().toString()));
            }

            if (order.getStatus() == OrderStatus.PAYMENT_INITIATED && order.getPayment() != null) {
                // Refund all recorded transactions
                for (UUID txId : order.getPayment().getWalletTransactionIds()) {
                    walletClient.refundTransaction(
                            new RefundRequest(txId, order.getId().toString()));
                }

                order.getPayment().setWalletTransactionIds(new ArrayList<>());
                order.getPayment().setStatus(PaymentStatus.FAILED);
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
        } catch (Exception ex) {
            log.error("COMPENSATION FAILED for order {}. Manual intervention required!", order.getId(), ex);
        }
    }

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> mapCartItemsToOrderItems(List<CartItem> cartItems) {
        Map<UUID, String> productNames = cartItems.stream()
                .map(CartItem::getProductId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> inventoryClient.getProductById(id).name()));

        return cartItems.stream()
                .map(ci -> OrderItem.builder()
                        .productId(ci.getProductId())
                        .productName(productNames.get(ci.getProductId()))
                        .quantity(ci.getQuantity())
                        .unitPrice(ci.getUnitPrice())
                        .subtotal(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .toList();
    }

    private void linkItemsToOrder(Order order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order); // Sets the FK back-reference in memory
            }
        }
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(i -> new OrderItemResponse(
                                i.getProductId(),
                                i.getProductName(), // Nullable; frontend handles gracefully
                                i.getQuantity(),
                                i.getUnitPrice(),
                                i.getSubtotal()))
                        .toList(),
                order.getCreatedAt());
    }

    private Map<UUID, BigDecimal> reconstructSellerPayouts(List<OrderItem> items) {
        Map<UUID, BigDecimal> payouts = new LinkedHashMap<>();
        for (OrderItem item : items) {
            UUID sellerId = inventoryClient.getSellerIdByProductId(item.getProductId());
            BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            payouts.merge(sellerId, total, BigDecimal::add);
        }
        return payouts;
    }

    public OrderStatus getOrderStatus(UUID orderId, UUID userId) {
        return orderRepo.findByIdAndUserId(orderId, userId)
                .map(Order::getStatus)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));
    }

    private List<StockItemRequest> mapCartItemsToStockRequests(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(ci -> new StockItemRequest(ci.getProductId(), ci.getQuantity()))
                .toList();
    }
}
