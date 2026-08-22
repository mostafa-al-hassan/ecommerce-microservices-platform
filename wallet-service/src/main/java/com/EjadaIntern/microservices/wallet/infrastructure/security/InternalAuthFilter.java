package com.EjadaIntern.microservices.wallet.infrastructure.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(1) // Runs BEFORE JwtAuthenticationFilter (which is typically Order 2+)
public class InternalAuthFilter extends OncePerRequestFilter {

    @Value("${internal.api.secret}")
    private String internalApiSecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only intercept /internal/** paths
        if (!path.startsWith("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-Internal-Api-Key");

        if (apiKey == null || !apiKey.equals(internalApiSecret)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or missing internal API key\"}");
            return;
        }

        // Key valid → proceed to controller
        filterChain.doFilter(request, response);
    }
}
