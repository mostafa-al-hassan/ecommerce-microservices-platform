package com.EjadaIntern.inventory_service.domain.port;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EjadaIntern.inventory_service.domain.model.StockTransaction;

public interface StockTransactionRepositoryPort {
    StockTransaction save(StockTransaction transaction);

    Page<StockTransaction> findByProductId(UUID productId, Pageable pageable);
}
