
package com.EjadaIntern.microservices.wallet.domain.port;

import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.microservices.wallet.domain.model.Wallet;

public interface WalletRepositoryPort {
    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID id);

}
