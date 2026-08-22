package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByOrderReferenceId(UUID orderReferenceId);
    List<Transaction> findByWalletId(UUID walletId);
}
