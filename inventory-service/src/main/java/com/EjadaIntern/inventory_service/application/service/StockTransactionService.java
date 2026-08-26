package com.EjadaIntern.inventory_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.inventory_service.application.dto.StockItemRequest;
import com.EjadaIntern.inventory_service.domain.model.Product;
import com.EjadaIntern.inventory_service.domain.model.StockTransaction;
import com.EjadaIntern.inventory_service.domain.model.TransactionType;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.ProductRepository;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.StockTransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public StockTransaction recordRestock(UUID productId, int quantity, String referenceId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        // Update product quantity
        product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
        productRepository.save(product);

        // record the transaction
        StockTransaction transaction = StockTransaction.builder()
                .product(product)
                .transactionType(TransactionType.RESTOCK)
                .quantity(quantity)
                .orderReferenceId(referenceId)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public StockTransaction recordSale(UUID productId, int quantity, String orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Sale quantity must be positive");
        }

        if (product.getQuantityAvailable() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: " + product.getQuantityAvailable()
                            + ", Requested: " + quantity);
        }

        // update product stock
        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
        productRepository.save(product);

        // Record the sale transaction
        StockTransaction transaction = StockTransaction.builder()
                .product(product)
                .transactionType(TransactionType.SALE)
                .quantity(-quantity) // Negative for sales
                .orderReferenceId(orderId)
                .build();

        return transactionRepository.save(transaction);
    }

    public Page<StockTransaction> getTransactionsByProduct(UUID productId, Pageable pageable) {
        return transactionRepository.findByProductId(productId, pageable);
    }

    public void validateStockAvailability(List<StockItemRequest> items) {
        for (StockItemRequest item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found: " + item.productId()));

            if (product.getQuantityAvailable() < item.quantity()) {
                throw new IllegalStateException(
                        String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                item.productId(), product.getQuantityAvailable(), item.quantity()));
            }
        }
    }

    @Transactional
    public void reserveStock(UUID productId, int quantity, String orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (product.getQuantityAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock for reservation");
        }

        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
        product.setQuantityReserved(product.getQuantityReserved() + quantity);

        productRepository.save(product);
    }

    @Transactional
    public void releaseReservedStock(UUID productId, int quantity, String orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (product.getQuantityReserved() < quantity) {
            throw new IllegalStateException("Reserved quantity for product: " + productId + "is less than the release request quantity");
        }

        product.setQuantityReserved(product.getQuantityReserved() - quantity);
        product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
        productRepository.save(product);
    }

    @Transactional
    public StockTransaction confirmSale(UUID productId, int quantity, String orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (product.getQuantityReserved() < quantity) {
            throw new IllegalStateException("Reserved stock not found for confirmation");
        }

        product.setQuantityReserved(product.getQuantityReserved() - quantity);
        productRepository.save(product);

        return transactionRepository.save(StockTransaction.builder()
                .product(product)
                .transactionType(TransactionType.SALE)
                .quantity(-quantity)
                .orderReferenceId(orderId)
                .build());
    }
}
