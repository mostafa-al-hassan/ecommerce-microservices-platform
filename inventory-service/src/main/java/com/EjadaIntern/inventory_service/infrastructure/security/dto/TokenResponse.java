package com.EjadaIntern.inventory_service.infrastructure.security.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String token;
    private String type;
    private long expiresIn; // in seconds
}
