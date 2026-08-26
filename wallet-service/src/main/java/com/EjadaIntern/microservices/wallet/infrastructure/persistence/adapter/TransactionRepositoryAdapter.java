package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;
import com.EjadaIntern.microservices.wallet.domain.port.TransactionRepositoryPort;
import com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionRepository transactionRepo;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepo.findById(id);
    }

    @Override
    public Page<Transaction> findByWalletId(UUID walletId, Pageable pageable) {
        return transactionRepo.findByWalletId(walletId, pageable);
    }

    @Override
    public boolean existsByOrderReferenceId(UUID orderReferenceId) {
        return transactionRepo.existsByOrderReferenceId(orderReferenceId);
    }

    @Override
    public Optional<Transaction> findByOrderReferenceId(UUID orderReferenceId) {
        return transactionRepo.findByOrderReferenceId(orderReferenceId);
    }

}
