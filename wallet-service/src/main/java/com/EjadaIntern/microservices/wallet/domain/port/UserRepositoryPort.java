package com.EjadaIntern.microservices.wallet.domain.port;

import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.microservices.wallet.domain.model.User;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByWalletId(UUID walletId);

    boolean existsByEmail(String email);

}
