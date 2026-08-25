package com.EjadaIntern.inventory_service.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EjadaIntern.inventory_service.application.dto.ProductDTO;
import com.EjadaIntern.inventory_service.domain.model.Category;
import com.EjadaIntern.inventory_service.domain.model.Product;
import com.EjadaIntern.inventory_service.domain.port.ImageStoragePort;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.CategoryRepository;
import com.EjadaIntern.inventory_service.infrastructure.persistence.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStoragePort imageStoragePort;

    // CreateProduct
    @Transactional
    public Product createProduct(ProductDTO productDTO,
            MultipartFile mainImage,
            List<MultipartFile> galleryImages) {
        String mainPath = uploadIfPresent(mainImage);
        List<String> galleryPaths = uploadAllIfPresent(galleryImages);

        Product product = mapToEntity(productDTO, mainPath, galleryPaths);

        return productRepository.save(product);
    }

    // UpdateProduct
    @Transactional
    public Product updateProduct(UUID id, ProductDTO dto,
            MultipartFile mainImage,
            List<MultipartFile> galleryImages) {
        Product existing = getProduct(id);

        String newMainPath = uploadIfPresent(mainImage);
        List<String> newGalleryPaths = uploadAllIfPresent(galleryImages);

        if (newMainPath != null && existing.getMainImagePath() != null) {
            imageStoragePort.delete(existing.getMainImagePath(), "inventory-images");
        }

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setSellerId(dto.sellerId());

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.categoryId()));
        existing.setCategory(category);

        if (newMainPath != null)
            existing.setMainImagePath(newMainPath);
        if (!newGalleryPaths.isEmpty())
            existing.setGalleryImagePaths(newGalleryPaths);

        return productRepository.save(existing);
    }

    // DeleteProduct
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProduct(id);

        if (product.getMainImagePath() != null) {
            imageStoragePort.delete(product.getMainImagePath(), "inventory-images");
        }
        if (product.getGalleryImagePaths() != null) {
            for (String path : product.getGalleryImagePaths()) {
                imageStoragePort.delete(path, "inventory-images");
            }
        }

        productRepository.delete(product);
    }

    // ReadProduct
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public Product getBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with SKU: " + sku));
    }

    private String uploadIfPresent(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return imageStoragePort.upload(file, "inventory-images");
        }
        return null;
    }

    private List<String> uploadAllIfPresent(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (!f.isEmpty()) {
                    paths.add(imageStoragePort.upload(f, "inventory-images"));
                }
            }
        }
        return paths;
    }

    private Product mapToEntity(ProductDTO productDTO, String mainPath, List<String> galleryPaths) {
        Category category = categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + productDTO.categoryId()));

        return Product.builder()
                .sku(productDTO.sku())
                .name(productDTO.name())
                .description(productDTO.description())
                .price(productDTO.price())
                .sellerId(productDTO.sellerId())
                .category(category)
                .quantityAvailable(productDTO.initialQuantity())
                .mainImagePath(mainPath)
                .galleryImagePaths(galleryPaths)
                .build();
    }
}
