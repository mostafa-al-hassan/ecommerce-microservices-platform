package com.EjadaIntern.inventory_service.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EjadaIntern.inventory_service.domain.model.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findBySellerId(UUID sellerId, Pageable pageable);

    @Query("SELECT p.price FROM Product p WHERE p.id = :id")
    Optional<BigDecimal> findPriceById(@Param("id") UUID id);

    @Query("SELECT p.sellerId FROM Product p WHERE p.id = :id")
    Optional<UUID> findSellerIdByProductId(@Param("id") UUID id);
}
