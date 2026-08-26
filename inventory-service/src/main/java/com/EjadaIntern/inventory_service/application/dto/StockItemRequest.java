package com.EjadaIntern.inventory_service.application.dto;

import java.util.UUID;

public record StockItemRequest(UUID productId, int quantity) {
}
