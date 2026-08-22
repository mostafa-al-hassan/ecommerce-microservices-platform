package com.EjadaIntern.microservices.wallet.application.dto;

public record AuthResponse(
    String token,
    String type
) {
    
}
