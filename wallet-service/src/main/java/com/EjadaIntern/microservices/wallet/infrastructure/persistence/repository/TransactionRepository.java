package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByOrderReferenceId(UUID orderReferenceId);

    Page<Transaction> findByWalletId(UUID walletId, Pageable pageable);

    Optional<Transaction> findByOrderReferenceId(UUID orderReferenceId);
}
