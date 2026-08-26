package com.EjadaIntern.inventory_service.application.dto;

import java.util.UUID;

public record StockSaleRequest(UUID productId, int quantity, String orderId) {
}
