package com.EjadaIntern.microservices.wallet.application.port;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

public interface TransactionServicePort {
    Transaction deposit(UUID userId, BigDecimal amount, UUID orderReferenceId);

    Transaction withdraw(UUID userId, BigDecimal amount, UUID orderReferenceId);

    Transaction transfer(UUID fromUserId, UUID toUserId, BigDecimal amount, UUID orderReferenceId);

    Page<Transaction> getHistory(UUID userId, Pageable pageable);

    BigDecimal getBalanceByUserId(UUID userId);

    Transaction refund(UUID originalTransactionId, UUID newOrderReferenceId);
}
