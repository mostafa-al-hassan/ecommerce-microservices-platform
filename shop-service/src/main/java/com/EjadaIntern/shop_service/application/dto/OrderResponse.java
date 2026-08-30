package com.EjadaIntern.shop_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.EjadaIntern.shop_service.domain.model.OrderStatus;

public record OrderResponse(
        UUID orderId,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt) {
}
