package com.EjadaIntern.shop_service.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.shop_service.application.dto.AddToCartRequest;
import com.EjadaIntern.shop_service.application.dto.CartItemResponse;
import com.EjadaIntern.shop_service.application.dto.CartResponse;
import com.EjadaIntern.shop_service.domain.dto.StockItemRequest;
import com.EjadaIntern.shop_service.domain.model.Cart;
import com.EjadaIntern.shop_service.domain.model.CartItem;
import com.EjadaIntern.shop_service.domain.model.CartStatus;
import com.EjadaIntern.shop_service.domain.port.CartRepositoryPort;
import com.EjadaIntern.shop_service.domain.port.InventoryClientPort;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepositoryPort cartRepo;
    private final InventoryClientPort inventoryClient;

    @Transactional
    public CartResponse getOrCreateActiveCart(UUID userId) {
        try {
            return mapToResponse(
                    cartRepo.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                            .orElseGet(() -> cartRepo.save(Cart.builder()
                                    .userId(userId)
                                    .status(CartStatus.ACTIVE)
                                    .build())));
        } catch (DataAccessException e) {
            // in case of another thread created it first
            Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalStateException("Cart creation failed"));
            return mapToResponse(cart);
        }
    }

    @Transactional
    public CartResponse addItem(UUID userId, AddToCartRequest request) {
        inventoryClient.validateStock(List.of(
                new StockItemRequest(request.productId(), request.quantity())));

        Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepo.save(Cart.builder()
                        .userId(userId)
                        .status(CartStatus.ACTIVE)
                        .build()));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.quantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.productId())
                    .quantity(request.quantity())
                    .unitPrice(fetchCurrentPrice(request.productId()))
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        return mapToResponse(cartRepo.save(cart));
    }

    @Transactional
    public void removeItem(UUID userId, UUID cartItemId) {
        Cart cart = cartRepo.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("No active cart"));
        cart.getItems().removeIf(item -> item.getProductId().equals(cartItemId));
        cartRepo.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {
        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()))
                .toList();

        return new CartResponse(cart.getId(), itemResponses, totalAmount);
    }

    private BigDecimal fetchCurrentPrice(UUID productId) {
        try {
            return inventoryClient.getProductPrice(productId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Product not found: " + productId);
        } catch (FeignException.ServiceUnavailable e) {
            throw new IllegalStateException("Inventory service unavailable; cannot validate price");
        }
    }
}
