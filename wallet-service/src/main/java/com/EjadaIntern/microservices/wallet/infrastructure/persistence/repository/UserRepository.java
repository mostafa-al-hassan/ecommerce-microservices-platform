package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.microservices.wallet.domain.model.User;

public interface UserRepository extends JpaRepository<User, UUID>{
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByWalletId(UUID walletId);
}
