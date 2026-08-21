package com.EjadaIntern.microservices.wallet.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EjadaIntern.microservices.wallet.domain.model.User;
import com.EjadaIntern.microservices.wallet.domain.port.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;

    public User login(String email, String rawPassword) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}
