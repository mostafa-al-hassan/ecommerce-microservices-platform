package com.EjadaIntern.microservices.wallet.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record WithdrawRequest(
    @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive")
    BigDecimal amount,
    UUID orderReferenceId
) {
    
}
