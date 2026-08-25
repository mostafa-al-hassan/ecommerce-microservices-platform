package com.EjadaIntern.inventory_service.infrastructure.persistence.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.inventory_service.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
