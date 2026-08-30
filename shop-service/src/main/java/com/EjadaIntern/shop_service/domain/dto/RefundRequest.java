package com.EjadaIntern.shop_service.domain.dto;

import java.util.UUID;

public record RefundRequest(
    UUID originalTransactionId,
    String orderReferenceId
) {}
