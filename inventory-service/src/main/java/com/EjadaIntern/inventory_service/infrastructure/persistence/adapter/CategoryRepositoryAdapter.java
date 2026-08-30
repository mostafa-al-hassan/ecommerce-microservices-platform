package com.EjadaIntern.inventory_service.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.EjadaIntern.inventory_service.domain.model.Category;
import com.EjadaIntern.inventory_service.domain.port.CategoryRepositoryPort;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);

    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public boolean existsById(UUID categoryId) {
        return existsById(categoryId);
    }

    @Override
    public List<Category> findBySellerId(UUID sellerId) {
        return categoryRepository.findBySellerId(sellerId);
    }
}
