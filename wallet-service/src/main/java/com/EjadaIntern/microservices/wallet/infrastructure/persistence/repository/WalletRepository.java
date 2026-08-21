package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.microservices.wallet.domain.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
