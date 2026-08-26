package com.EjadaIntern.inventory_service.domain.port;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EjadaIntern.inventory_service.domain.model.Product;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    void delete(Product product);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findBySellerId(UUID sellerId, Pageable pageable);
}
