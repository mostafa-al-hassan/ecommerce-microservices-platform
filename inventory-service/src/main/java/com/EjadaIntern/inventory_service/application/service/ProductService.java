package com.EjadaIntern.inventory_service.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EjadaIntern.inventory_service.application.dto.ProductDTO;
import com.EjadaIntern.inventory_service.domain.model.Category;
import com.EjadaIntern.inventory_service.domain.model.Product;
import com.EjadaIntern.inventory_service.domain.port.CategoryRepositoryPort;
import com.EjadaIntern.inventory_service.domain.port.ImageStoragePort;
import com.EjadaIntern.inventory_service.domain.port.ProductRepositoryPort;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final ImageStoragePort imageStoragePort;

    @Value("${app.storage.bucket:inventory-images}")
    private String bucketName;

    @Transactional
    public Product createProduct(ProductDTO productDTO,
            MultipartFile mainImage,
            List<MultipartFile> galleryImages) {

        if (productRepository.findBySku(productDTO.sku()).isPresent()) {
            throw new IllegalStateException(
                    "A product with SKU '" + productDTO.sku() + "' already exists.");
        }

        String mainPath = uploadIfPresent(mainImage);
        List<String> galleryPaths = uploadAllIfPresent(galleryImages);

        Product product = mapToEntity(productDTO, mainPath, galleryPaths);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, ProductDTO dto,
            MultipartFile mainImage,
            List<MultipartFile> galleryImages) {
        Product existing = getProduct(id);

        if (!existing.getSellerId().equals(dto.sellerId())) {
            throw new AccessDeniedException("Cannot transfer product ownership during update");
        }

        String newMainPath = uploadIfPresent(mainImage);
        List<String> newGalleryPaths = uploadAllIfPresent(galleryImages);

        if (newMainPath != null && existing.getMainImagePath() != null) {
            imageStoragePort.delete(existing.getMainImagePath());
        }

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setSellerId(dto.sellerId());
        existing.setQuantityAvailable(dto.quantityAvailable());

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.categoryId()));
        existing.setCategory(category);

        if (newMainPath != null)
            existing.setMainImagePath(newMainPath);
        if (!newGalleryPaths.isEmpty())
            existing.setGalleryImagePaths(newGalleryPaths);

        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProduct(id);

        if (product.getMainImagePath() != null) {
            imageStoragePort.delete(product.getMainImagePath());
        }
        if (product.getGalleryImagePaths() != null) {
            for (String path : product.getGalleryImagePaths()) {
                imageStoragePort.delete(path);
            }
        }

        productRepository.delete(product);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public BigDecimal getProductPrice(UUID id) {
        return productRepository.findPriceById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public Product getBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with SKU: " + sku));
    }

    public UUID getSellerIdByProductId(UUID productId) {
        return productRepository.findSellerIdByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    private String uploadIfPresent(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return imageStoragePort.upload(file);
        }
        return null;
    }

    private List<String> uploadAllIfPresent(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (!f.isEmpty()) {
                    paths.add(imageStoragePort.upload(f));
                }
            }
        }
        return paths;
    }

    public Page<Product> getProductsBySeller(UUID sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable);
    }

    public ProductDTO mapToDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSellerId(),
                product.getCategory().getId(),
                product.getQuantityAvailable(),
                product.getMainImagePath(),
                product.getGalleryImagePaths(),
                product.getCreatedAt());
    }

    public Product mapToEntity(ProductDTO dto, String mainPath, List<String> galleryPaths) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return Product.builder()
                .sku(dto.sku())
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .sellerId(dto.sellerId())
                .category(category)
                .quantityAvailable(dto.quantityAvailable() != null ? dto.quantityAvailable() : 0)
                .mainImagePath(mainPath)
                .galleryImagePaths(galleryPaths)
                .build();
    }
}
