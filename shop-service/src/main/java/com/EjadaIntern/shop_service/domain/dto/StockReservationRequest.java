package com.EjadaIntern.shop_service.domain.dto;

import java.util.UUID;

public record StockReservationRequest(UUID productId, int quantity, String orderId) {
}
