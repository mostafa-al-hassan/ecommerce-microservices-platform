package com.EjadaIntern.inventory_service.infrastructure.persistence.adapter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.EjadaIntern.inventory_service.domain.port.ImageStoragePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioImageStorageAdapter implements ImageStoragePort {

    private final S3Client s3Client;

    @Value("${app.storage.bucket:inventory-images}")
    private String defaultBucket;

    @Value("${app.storage.endpoint:http://localhost:9000}")
    private String endpoint; // ← Add this to application.yml

    @Override
    public String upload(MultipartFile file) {
        String uniqueKey = UUID.randomUUID() + "_" +
                StringUtils.cleanPath(file.getOriginalFilename());

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(defaultBucket)
                .key(uniqueKey)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));
            return uniqueKey;

        } catch (S3Exception e) {
            // Specific S3 errors (404 bucket, 403 access denied, etc.)
            log.error("S3 upload failed for key {}: {} ({})",
                    uniqueKey, e.awsErrorDetails().errorMessage(), e.statusCode());

            if (e.statusCode() == 404) {
                throw new IllegalStateException(
                        "Storage bucket '" + defaultBucket + "' does not exist. " +
                                "Please create it in MinIO console or check app.storage.bucket config.",
                        e);
            }
            if (e.statusCode() == 403) {
                throw new IllegalStateException(
                        "Access denied to bucket '" + defaultBucket + "'. " +
                                "Verify MINIO_ROOT_USER/MINIO_ROOT_PASSWORD credentials.",
                        e);
            }
            throw new IllegalStateException(
                    "Failed to upload image: " + e.awsErrorDetails().errorMessage(), e);

        } catch (IOException e) {
            // File read/stream errors
            log.error("Failed to read uploaded file {}", uniqueKey, e);
            throw new IllegalArgumentException("Invalid or corrupted upload file", e);

        } catch (SdkClientException e) {
            // Network/connectivity issues
            log.error("S3 client error during upload of {}", uniqueKey, e);
            throw new IllegalStateException(
                    "Storage service unavailable. Please try again later.", e);
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(defaultBucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    @Override
    public String generateUrl(String key) {
        return endpoint + "/" + defaultBucket + "/" + key;
    }
}
