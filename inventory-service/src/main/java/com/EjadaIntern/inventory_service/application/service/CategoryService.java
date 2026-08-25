package com.EjadaIntern.inventory_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.inventory_service.domain.model.Category;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // CreateCategory
    @Transactional
    public Category createCategory(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    // UpdateCategory
    @Transactional
    public Category updateCategory(UUID id, String name, String description) {
        Category existing = getCategory(id);

        existing.setName(name);
        existing.setDescription(description);

        return categoryRepository.save(existing);
    }

    // DeleteCategory
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategory(id);
        categoryRepository.delete(category);
    }

    public Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public boolean exists(UUID id) {
        return categoryRepository.existsById(id);
    }
}
