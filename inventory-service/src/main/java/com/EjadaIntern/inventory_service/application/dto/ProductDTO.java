package com.EjadaIntern.inventory_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDTO(
    UUID id, // Will be null on Create, populated on Read/Update

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU cannot exceed 50 characters")
    String sku,

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    String name,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be positive")
    BigDecimal price,

    @NotNull(message = "Seller ID is required")
    UUID sellerId,

    @NotNull(message = "Category ID is required")
    UUID categoryId,

    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantityAvailable,

    String mainImagePath,
    List<String> galleryImagePaths,

    LocalDateTime createdAt
) {}
