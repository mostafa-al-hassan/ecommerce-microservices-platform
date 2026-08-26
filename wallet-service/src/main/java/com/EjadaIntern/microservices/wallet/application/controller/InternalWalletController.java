package com.EjadaIntern.microservices.wallet.application.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.microservices.wallet.application.dto.RefundRequest;
import com.EjadaIntern.microservices.wallet.application.dto.TransferRequest;
import com.EjadaIntern.microservices.wallet.application.port.TransactionServicePort;
import com.EjadaIntern.microservices.wallet.domain.model.Transaction;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/wallets")
@RequiredArgsConstructor
public class InternalWalletController {

    private final TransactionServicePort transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            Transaction tx = transactionService.transfer(
                    request.fromUserId(),
                    request.toUserId(),
                    request.amount(),
                    request.orderReferenceId());
            return ResponseEntity.ok(Map.of(
                    "transactionId", tx.getId(),
                    "status", tx.getStatus(),
                    "balanceAfter", tx.getBalanceAfter()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable UUID userId) {
        try {
            BigDecimal balance = transactionService.getBalanceByUserId(userId);
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "balance", balance,
                    "currency", "USD"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@Valid @RequestBody RefundRequest request) {
        try {
            var tx = transactionService.refund(
                    request.originalTransactionId(),
                    request.orderReferenceId());
            return ResponseEntity.ok(Map.of(
                    "refundTransactionId", tx.getId(),
                    "refundedAmount", tx.getAmount(),
                    "status", tx.getStatus()));
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 
     * test
     * 
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Hello from Wallet Service Internal API",
                "timestamp", java.time.LocalDateTime.now().toString()));
    }

}
