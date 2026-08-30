package com.EjadaIntern.microservices.wallet.application.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

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

    // 422 — Business rule violation (insufficient funds, invalid state, etc.)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(IllegalStateException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());

        String message = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT; // 422

        // Optional: Map specific messages to more precise statuses
        if (message.contains("Insufficient funds")) {
            status = HttpStatus.PAYMENT_REQUIRED; // 402
            message = "Insufficient wallet balance to complete transfer";
        }

        return ResponseEntity.status(status)
                .body(errorBody("Business Rule Violation", message));
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
