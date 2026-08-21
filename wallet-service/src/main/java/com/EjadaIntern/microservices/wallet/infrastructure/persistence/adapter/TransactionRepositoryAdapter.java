package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;
import com.EjadaIntern.microservices.wallet.domain.port.TransactionRepositoryPort;
import com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository.TransactionRepository;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionRepository transactionRepo;

    public TransactionRepositoryAdapter(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepo.findById(id);
    }

    @Override
    public List<Transaction> findByWalletId(UUID walletId) {
        return transactionRepo.findByWalletId(walletId);
    }

}
