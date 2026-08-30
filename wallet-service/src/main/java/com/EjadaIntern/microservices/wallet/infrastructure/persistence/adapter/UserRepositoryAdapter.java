package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.EjadaIntern.microservices.wallet.domain.model.User;
import com.EjadaIntern.microservices.wallet.domain.port.UserRepositoryPort;
import com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort{

    private final UserRepository userRepo;

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Optional<User> findByWalletId(UUID walletId) {
        return userRepo.findByWalletId(walletId);
    }

}
