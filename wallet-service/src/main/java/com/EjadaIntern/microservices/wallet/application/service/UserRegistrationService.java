package com.EjadaIntern.microservices.wallet.application.service;

import java.math.BigDecimal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EjadaIntern.microservices.wallet.domain.model.Role;
import com.EjadaIntern.microservices.wallet.domain.model.User;
import com.EjadaIntern.microservices.wallet.domain.model.Wallet;
import com.EjadaIntern.microservices.wallet.domain.port.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;

    public User register(String email, String rawPassword, String firstName, String lastName, Role role) {

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already Exists");
        }

        Wallet wallet = Wallet.builder().balance(BigDecimal.ZERO).build();

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .wallet(wallet)
                .build();

        return userRepo.save(user);
    }
}
