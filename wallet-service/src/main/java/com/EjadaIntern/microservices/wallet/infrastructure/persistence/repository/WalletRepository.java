package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EjadaIntern.microservices.wallet.domain.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query("SELECT w FROM Wallet w JOIN User u ON u.wallet.id = w.id WHERE u.id = :userId")
    Optional<Wallet> findByUserId(@Param("userId") UUID userId);
}
