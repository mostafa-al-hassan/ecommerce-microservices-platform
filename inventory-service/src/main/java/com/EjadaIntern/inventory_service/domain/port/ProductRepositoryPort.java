package com.EjadaIntern.inventory_service.domain.port;

import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.inventory_service.domain.model.Product;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

}
