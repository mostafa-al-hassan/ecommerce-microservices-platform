package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.EjadaIntern.microservices.wallet.domain.model.Wallet;
import com.EjadaIntern.microservices.wallet.domain.port.WalletRepositoryPort;
import com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final WalletRepository walletRepo;

    @Override
    public Wallet save(Wallet wallet) {
        return walletRepo.save(wallet);
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return walletRepo.findById(id);
    }

    @Override
    public Optional<Wallet> findByUserId(UUID userId) {
        return walletRepo.findByUserId(userId);
    }

}
