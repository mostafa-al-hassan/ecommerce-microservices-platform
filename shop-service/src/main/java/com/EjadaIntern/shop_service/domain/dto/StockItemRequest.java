package com.EjadaIntern.shop_service.domain.dto;

import java.util.UUID;

public record StockItemRequest(
    UUID productId,
    int quantity
) {}
