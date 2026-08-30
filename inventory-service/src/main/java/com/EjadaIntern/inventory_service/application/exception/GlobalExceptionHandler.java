package com.EjadaIntern.inventory_service.application.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
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

    // 409 — Conflict (database constraint violations like duplicate SKU)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        String message = extractDbErrorMessage(ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorBody("Conflict", message));
    }

    private String extractDbErrorMessage(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof SQLIntegrityConstraintViolationException sqlEx) {
            String msg = sqlEx.getMessage().toLowerCase();
            if (msg.contains("duplicate") || msg.contains("unique")) {
                return "A product with this SKU already exists. Please use a unique SKU.";
            }
            if (msg.contains("foreign key")) {
                return "Referenced category or seller does not exist.";
            }
        }
        return "Database constraint violation. Please check your input data.";
    }

    // 422 — Unprocessable Entity (business rule violation, e.g., insufficient
    // balance)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(errorBody("Unprocessable Entity", ex.getMessage()));
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
