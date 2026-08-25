package com.EjadaIntern.inventory_service.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import com.EjadaIntern.inventory_service.domain.port.ImageStoragePort;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioImageStorageAdapter implements ImageStoragePort {

    private final S3Client s3Client;

    @Value("${app.storage.bucket:inventory-images}")
    private String defaultBucket;

    @Override
    public String upload(MultipartFile file, String bucketName) {
        try {
            // Generate unique key to prevent collisions & path traversal
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueKey = UUID.randomUUID() + "_" + originalFilename;
            String targetBucket = (bucketName != null && !bucketName.isBlank())
                    ? bucketName
                    : defaultBucket;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(uniqueKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            return uniqueKey; // Store ONLY the key in DB, never full URL

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key, String bucketName) {
        try {
            String targetBucket = (bucketName != null && !bucketName.isBlank())
                    ? bucketName
                    : defaultBucket;

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            // Log but don't fail product deletion if image cleanup fails
            System.err.println("Warning: Failed to delete orphaned image ["
                    + key + "]: " + e.getMessage());
        }
    }
}

