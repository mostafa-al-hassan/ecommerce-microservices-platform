package com.EjadaIntern.shop_service.application.dto;

import java.util.UUID;

public record AddToCartRequest(
    UUID productId,
    int quantity
) {}
