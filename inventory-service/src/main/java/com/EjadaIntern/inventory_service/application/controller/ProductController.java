package com.EjadaIntern.inventory_service.application.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EjadaIntern.inventory_service.application.dto.CategoryResponse;
import com.EjadaIntern.inventory_service.application.dto.CreateCategoryRequest;
import com.EjadaIntern.inventory_service.application.dto.ProductDTO;
import com.EjadaIntern.inventory_service.application.dto.UpdateCategoryRequest;
import com.EjadaIntern.inventory_service.application.service.CategoryService;
import com.EjadaIntern.inventory_service.application.service.ProductService;
import com.EjadaIntern.inventory_service.domain.model.Category;
import com.EjadaIntern.inventory_service.domain.model.Product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping("/categories")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal String sellerId) {

        Category category = categoryService.createCategory(
                UUID.fromString(sellerId),
                request.name(),
                request.description());

        CategoryResponse response = categoryService.mapToResponse(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories.stream()
                .map(categoryService::mapToResponse)
                .toList());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok(categoryService.mapToResponse(category));
    }

    @GetMapping("/my-categories")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<CategoryResponse>> getMyCategories(
            @AuthenticationPrincipal String sellerId) {

        List<Category> categories = categoryService.getCategoriesBySeller(
                UUID.fromString(sellerId));
        return ResponseEntity.ok(categories.stream()
                .map(categoryService::mapToResponse)
                .toList());
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request,
            @AuthenticationPrincipal String sellerId) {

        Category updated = categoryService.updateCategory(
                id, UUID.fromString(sellerId), request.name(), request.description());
        return ResponseEntity.ok(categoryService.mapToResponse(updated));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal String sellerId) {

        categoryService.deleteCategory(id, UUID.fromString(sellerId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") @Valid ProductDTO product,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        Product created = productService.createProduct(product, mainImage, galleryImages);
        return ResponseEntity.status(201).body(productService.mapToDto(created));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products.map(productService::mapToDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(productService.mapToDto(product));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getBySku(@PathVariable String sku) {
        Product product = productService.getBySku(sku);
        return ResponseEntity.ok(productService.mapToDto(product));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable UUID id,
            @RequestPart("product") ProductDTO product,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        Product updated = productService.updateProduct(id, product, mainImage, galleryImages);
        return ResponseEntity.ok(productService.mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<ProductDTO>> getMyProducts(
            Pageable pageable,
            @org.springframework.security.core.annotation.AuthenticationPrincipal String sellerId) {

        Page<Product> products = productService.getProductsBySeller(UUID.fromString(sellerId), pageable);
        return ResponseEntity.ok(products.map(productService::mapToDto));
    }
}
