package com.EjadaIntern.microservices.wallet.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletBalanceResponse(
    UUID walletId,
    BigDecimal balance,
    LocalDateTime updatedAt
) {}
