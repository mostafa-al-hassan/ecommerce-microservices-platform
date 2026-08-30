package com.EjadaIntern.inventory_service.application.dto;

import java.util.UUID;

public record CategoryResponse(UUID id, UUID sellerId, String name, String description) {
}
