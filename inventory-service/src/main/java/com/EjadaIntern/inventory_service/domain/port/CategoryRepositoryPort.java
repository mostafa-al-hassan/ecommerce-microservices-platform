package com.EjadaIntern.inventory_service.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.inventory_service.domain.model.Category;

public interface CategoryRepositoryPort {
    Category save(Category category);

    Optional<Category> findById(UUID categoryId);

    boolean existsById(UUID categoryId);

    List<Category> findBySellerId(UUID sellerId);

}
