package com.EjadaIntern.shop_service.infrastructure.presistance.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EjadaIntern.shop_service.domain.model.Cart;
import com.EjadaIntern.shop_service.domain.model.CartStatus;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    List<Cart> findAllByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime cutoff);
}
