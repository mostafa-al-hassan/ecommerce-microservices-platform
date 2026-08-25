package com.EjadaIntern.inventory_service.domain.port;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {

    String upload(MultipartFile file, String bucketName);

    void delete(String key, String bucketName);
}
