package com.EjadaIntern.microservices.wallet.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.EjadaIntern.microservices.wallet.domain.model.TransactionStatus;
import com.EjadaIntern.microservices.wallet.domain.model.TransactionType;

public record TransactionHistoryResponse(
    UUID id,
    TransactionType type,
    BigDecimal amount,
    TransactionStatus status,
    BigDecimal balanceAfter,
    LocalDateTime createdAt
) {}
