package com.EjadaIntern.shop_service.infrastructure.presistance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EjadaIntern.shop_service.domain.model.Order;
import com.EjadaIntern.shop_service.domain.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status IN (:statuses)")
    List<Order> findActiveOrdersByUserId(
            @Param("userId") UUID userId,
            @Param("statuses") List<OrderStatus> statuses);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
