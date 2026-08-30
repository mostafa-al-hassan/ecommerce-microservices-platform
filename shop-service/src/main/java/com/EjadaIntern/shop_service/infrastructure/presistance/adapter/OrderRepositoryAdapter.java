package com.EjadaIntern.shop_service.infrastructure.presistance.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.EjadaIntern.shop_service.domain.model.Order;
import com.EjadaIntern.shop_service.domain.model.OrderStatus;
import com.EjadaIntern.shop_service.domain.port.OrderRepositoryPort;
import com.EjadaIntern.shop_service.infrastructure.presistance.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepo;

    @Override
    public Order save(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderRepo.findById(orderId);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepo.findByOrderNumber(orderNumber);
    }

    @Override
    public Page<Order> findByUserId(UUID userId, Pageable pageable) {
        return orderRepo.findByUserId(userId, pageable);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return orderRepo.findByStatus(status, pageable);
    }

    @Override
    public Optional<Order> findByIdAndUserId(UUID orderId, UUID userId) {
        return orderRepo.findByIdAndUserId(orderId, userId);
    }

    @Override
    public List<Order> findActiveOrdersByUserId(UUID userId) {
        return orderRepo.findActiveOrdersByUserId(userId, List.of(OrderStatus.PENDING, OrderStatus.PAYMENT_INITIATED));
    }
}
