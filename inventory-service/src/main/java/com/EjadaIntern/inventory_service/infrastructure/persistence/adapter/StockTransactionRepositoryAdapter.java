package com.EjadaIntern.inventory_service.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.EjadaIntern.inventory_service.domain.model.StockTransaction;
import com.EjadaIntern.inventory_service.domain.port.StockTransactionRepositoryPort;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.StockTransactionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockTransactionRepositoryAdapter implements StockTransactionRepositoryPort {

    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public StockTransaction save(StockTransaction transaction) {
        return stockTransactionRepository.save(transaction);
    }

    @Override
    public Page<StockTransaction> findByProductId(UUID productId, Pageable pageable) {
        return stockTransactionRepository.findByProductId(productId, pageable);
    }

}
