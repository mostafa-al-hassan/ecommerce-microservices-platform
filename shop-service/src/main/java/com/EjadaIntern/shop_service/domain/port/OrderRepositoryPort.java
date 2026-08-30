package com.EjadaIntern.shop_service.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EjadaIntern.shop_service.domain.model.Order;
import com.EjadaIntern.shop_service.domain.model.OrderStatus;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);

    List<Order> findActiveOrdersByUserId(UUID userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
