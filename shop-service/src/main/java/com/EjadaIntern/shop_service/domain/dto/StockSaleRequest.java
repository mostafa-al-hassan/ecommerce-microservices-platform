package com.EjadaIntern.shop_service.domain.dto;

import java.util.UUID;

public record StockSaleRequest(UUID productId, int quantity, String orderId) {
}
