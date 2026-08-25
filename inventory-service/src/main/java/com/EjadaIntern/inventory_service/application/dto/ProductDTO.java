package com.EjadaIntern.inventory_service.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(

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

    @Min(value = 0, message = "Initial quantity cannot be negative")
    Integer initialQuantity
) {}
