package com.EjadaIntern.inventory_service.domain.port;

import com.EjadaIntern.inventory_service.domain.model.Category;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);

    Optional<Category> findById(UUID categoryId);

    boolean existsById(UUID categoryId);
}
