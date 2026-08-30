package com.EjadaIntern.inventory_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.inventory_service.application.dto.CategoryResponse;
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
    public Category createCategory(UUID sellerId, String name, String description) {
        Category category = Category.builder()
                .sellerId(sellerId)
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    // UpdateCategory
    @Transactional
    public Category updateCategory(UUID id, UUID sellerId, String name, String description) {
        Category existing = getCategory(id);

        System.out.println(sellerId + " " + existing.getSellerId());

        if (!existing.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Cannot modify another seller's category");
        }

        existing.setName(name);
        existing.setDescription(description);

        return categoryRepository.save(existing);
    }

    // DeleteCategory
    @Transactional
    public void deleteCategory(UUID id, UUID sellerId) {
        Category category = getCategory(id);

        if (!category.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Cannot modify another seller's category");
        }

        categoryRepository.delete(category);
    }

    public Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    public Category getSellerCategory(UUID id, UUID sellerId) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public boolean exists(UUID id) {
        return categoryRepository.existsById(id);
    }

    public CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getSellerId(),
                category.getName(),
                category.getDescription());
    }

    public List<Category> getCategoriesBySeller(UUID sellerId) {
        return categoryRepository.findBySellerId(sellerId);
    }

}
