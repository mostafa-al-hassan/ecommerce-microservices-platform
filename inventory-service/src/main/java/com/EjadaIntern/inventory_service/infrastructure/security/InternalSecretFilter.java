package com.EjadaIntern.inventory_service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalSecretFilter extends OncePerRequestFilter {

    @Value("${internal.api.secret}")
    private String expectedSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/internal/")) {
            String providedSecret = request.getHeader("X-Internal-Api-Key");

            if (providedSecret == null || !providedSecret.equals(expectedSecret)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Internal Secret");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
