package com.EjadaIntern.inventory_service.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.EjadaIntern.inventory_service.infrastructure.security.dto.TokenResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceTokenManager {

    @Value("${app.security.service.client-id}")
    private String clientId;
    @Value("${app.security.service.client-secret}")
    private String clientSecret;
    @Value("${wallet.service.url}")
    private String walletUrl;
    @Value("${app.security.token-refresh-buffer:30}")
    private long refreshBufferSeconds;

    private final RestTemplate restTemplate = new RestTemplate();

    // ensures only one request can make a refresh call at a time
    private final ReentrantLock lock = new ReentrantLock();

    // a private static class to hold the token
    private volatile TokenCache tokenCache;

    public String getValidToken() {
        // If token is valid, return it
        if (tokenCache != null && !tokenCache.isExpired()) {
            return tokenCache.getToken();
        }

        // Acquire the lock
        // waits here and wait if some other thread is already in the critical section
        lock.lock();
        // critical section
        try {
            // Maybe the thread that got the lock first already updated it
            if (tokenCache != null && !tokenCache.isExpired()) {
                return tokenCache.getToken();
            }

            // Perform the refresh
            refreshToken();
            return tokenCache.getToken();

        } finally {
            // release the lock so others can proceed
            lock.unlock();
        }
    }

    private void refreshToken() {
        String url = walletUrl + "/api/service-auth/service-token";
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(url, entity, TokenResponse.class);
            TokenResponse body = response.getBody();
            if (body != null) {
                tokenCache = new TokenCache(body.getToken(), body.getExpiresIn(), refreshBufferSeconds);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Failed to fetch service token from Wallet", e);
        }
    }

    private static class TokenCache {
        private final String token;
        private final long expiryTimeMillis;

        public TokenCache(String token, long expiresInSeconds, long refreshBufferSeconds) {
            this.token = token;
            this.expiryTimeMillis = System.currentTimeMillis() + ((expiresInSeconds - refreshBufferSeconds) * 1000);
        }

        public String getToken() {
            return token;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTimeMillis;
        }
    }
}
