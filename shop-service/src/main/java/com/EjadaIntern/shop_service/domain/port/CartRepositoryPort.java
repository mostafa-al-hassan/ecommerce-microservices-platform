package com.EjadaIntern.shop_service.domain.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.shop_service.domain.model.Cart;
import com.EjadaIntern.shop_service.domain.model.CartStatus;

public interface CartRepositoryPort {
    Cart save(Cart cart);

    Optional<Cart> findById(UUID cartId);

    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    void deleteById(UUID cartId);

    List<Cart> findAllByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime cutoff);
}
