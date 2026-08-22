package com.EjadaIntern.microservices.wallet.domain.port;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Page<Transaction> findByWalletId(UUID walletId, Pageable pageable);

    boolean existsByOrderReferenceId(UUID orderReferenceId);

    Optional<Transaction> findByOrderReferenceId(UUID orderReferenceId);

}
