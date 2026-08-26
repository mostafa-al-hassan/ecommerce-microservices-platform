package com.EjadaIntern.microservices.wallet.application.service;

import com.EjadaIntern.microservices.wallet.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public String getCurrentAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated service client");
        }
        return auth.getName(); // the 'name' is the client_id
    }

    public String generateServiceToken(String clientId) {
        return jwtTokenProvider.generateToken(clientId, "SERVICE");
    }
}
