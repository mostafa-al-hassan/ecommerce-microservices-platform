package com.EjadaIntern.shop_service.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundResponse(
    UUID refundTransactionId,
    BigDecimal refundedAmount,
    String status
) {}
