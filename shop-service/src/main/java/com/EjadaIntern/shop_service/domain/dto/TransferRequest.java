package com.EjadaIntern.shop_service.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransferRequest(
    @NotNull(message = "Sender user ID is required")
    UUID fromUserId,

    @NotNull(message = "Receiver user ID is required")
    UUID toUserId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Order reference ID is mandatory for internal transfers")
    UUID orderReferenceId
) {}
