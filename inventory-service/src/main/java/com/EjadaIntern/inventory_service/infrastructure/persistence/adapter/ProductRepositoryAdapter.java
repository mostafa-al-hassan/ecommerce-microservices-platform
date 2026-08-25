package com.EjadaIntern.inventory_service.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.inventory_service.domain.model.Product;
import com.EjadaIntern.inventory_service.domain.port.ProductRepositoryPort;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }
}
