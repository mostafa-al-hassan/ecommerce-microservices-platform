package com.EjadaIntern.shop_service.infrastructure.presistance.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.EjadaIntern.shop_service.domain.model.Cart;
import com.EjadaIntern.shop_service.domain.model.CartStatus;
import com.EjadaIntern.shop_service.domain.port.CartRepositoryPort;
import com.EjadaIntern.shop_service.infrastructure.presistance.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartRepository cartRepo;

    @Override
    public Cart save(Cart cart) {
        return cartRepo.save(cart);
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {
        return cartRepo.findById(cartId);
    }

    @Override
    public Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status) {
        return cartRepo.findByUserIdAndStatus(userId, status);
    }

    @Override
    public void deleteById(UUID cartId) {
        cartRepo.deleteById(cartId);
    }

    @Override
    public List<Cart> findAllByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime cutoff) {
        return cartRepo.findAllByStatusAndUpdatedAtBefore(status, cutoff);
    }
}
