package com.EjadaIntern.shop_service.application.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 — Bad Request (validation errors, invalid arguments)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody("Bad Request", ex.getMessage()));
    }

    // 400 — Validation errors (@Valid on DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody("Validation Failed", message));
    }

    // 404 — Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody("Not Found", ex.getMessage()));
    }

    // 422 — Unprocessable Entity (business rule violation, e.g., insufficient
    // balance)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(errorBody("Unprocessable Entity", ex.getMessage()));
    }

    // Handle Feign errors from downstream services
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignError(FeignException ex) {
        int status = ex.status();

        // Pass through the downstream service's error response if available
        if (ex.contentUTF8() != null && !ex.contentUTF8().isEmpty()) {
            try {
                Map<String, Object> downstreamError = new ObjectMapper()
                        .readValue(ex.contentUTF8(), new TypeReference<>() {
                        });
                return ResponseEntity.status(status).body(downstreamError);
            } catch (Exception parseEx) {
                log.warn("Failed to parse Feign error body", parseEx);
            }
        }

        // Fallback if no parseable body
        return ResponseEntity.status(status)
                .body(errorBody("Downstream Service Error",
                        "Wallet service returned " + status + ": " + ex.getMessage()));
    }

    // 500 — Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal Server Error", "Something went wrong"));
    }

    private Map<String, Object> errorBody(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", Instant.now().toString());
    }
}
