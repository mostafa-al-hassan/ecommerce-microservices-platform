package com.EjadaIntern.inventory_service.application.controller;

import com.EjadaIntern.inventory_service.application.dto.ProductDTO;
import com.EjadaIntern.inventory_service.application.service.ProductService;
import com.EjadaIntern.inventory_service.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") ProductDTO product,
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
