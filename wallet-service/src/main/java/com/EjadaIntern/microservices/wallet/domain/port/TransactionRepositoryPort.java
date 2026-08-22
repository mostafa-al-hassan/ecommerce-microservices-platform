package com.EjadaIntern.microservices.wallet.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByWalletId(UUID walletId);

    boolean existsByOrderReferenceId(UUID orderReferenceId);
}
