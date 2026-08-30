package com.EjadaIntern.shop_service.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String status,
        BigDecimal balanceAfter) {
}
