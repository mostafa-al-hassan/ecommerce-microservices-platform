package com.EjadaIntern.inventory_service.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.inventory_service.domain.model.StockTransaction;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, UUID> {

    Page<StockTransaction> findByProductId(UUID productId, Pageable pageable);
}
