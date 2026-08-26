package com.EjadaIntern.microservices.wallet.application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.microservices.wallet.application.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/service-auth")
@RequiredArgsConstructor
public class ServiceTokenController {

    private final AuthService authService;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Endpoint for microservices to request a JWT.
     * Protected by HTTP Basic Auth (Client ID + Client Secret).
     */
    @PostMapping("/service-token")
    @PreAuthorize("hasRole('SERVICE_CLIENT')") // Ensures only authenticated services reach here
    public ResponseEntity<Map<String, Object>> getServiceToken() {
        // The authentication is already handled by Spring Security's Basic Auth filter.
        // We just need to extract the client_id from the current security context.
        String clientId = authService.getCurrentAuthenticatedClientId();

        String token = authService.generateServiceToken(clientId);
        long expiresInSeconds = jwtExpirationMs / 1000;

        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "expiresIn", expiresInSeconds));
    }
}
