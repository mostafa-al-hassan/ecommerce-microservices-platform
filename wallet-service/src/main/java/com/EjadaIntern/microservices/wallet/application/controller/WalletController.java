package com.EjadaIntern.microservices.wallet.application.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.microservices.wallet.application.dto.DepositRequest;
import com.EjadaIntern.microservices.wallet.application.dto.WithdrawRequest;
import com.EjadaIntern.microservices.wallet.application.dto.TransactionHistoryResponse;
import com.EjadaIntern.microservices.wallet.application.dto.WalletBalanceResponse;
import com.EjadaIntern.microservices.wallet.application.port.TransactionServicePort;
import com.EjadaIntern.microservices.wallet.domain.model.Transaction;
import com.EjadaIntern.microservices.wallet.domain.model.Wallet;
import com.EjadaIntern.microservices.wallet.domain.port.WalletRepositoryPort;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final TransactionServicePort transactionService;
    private final WalletRepositoryPort walletRepo;

    @GetMapping("/getWallet")
    public ResponseEntity<?> getUserWallet(@AuthenticationPrincipal String userId) {
        try {
            Wallet wallet = walletRepo.findByUserId(UUID.fromString(userId))
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

            WalletBalanceResponse response = new WalletBalanceResponse(
                    wallet.getId(),
                    wallet.getBalance(),
                    wallet.getUpdatedAt());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @AuthenticationPrincipal String userId,
            @RequestBody DepositRequest request) {
        try {
            BigDecimal amount = new BigDecimal(request.amount().toString());
            UUID orderRefId = request.orderReferenceId() != null
                    ? UUID.fromString(request.orderReferenceId().toString())
                    : null;

            var tx = transactionService.deposit(UUID.fromString(userId), amount, orderRefId);
            return ResponseEntity.ok(new TransactionHistoryResponse(
                    tx.getId(), tx.getType(), tx.getAmount(),
                    tx.getStatus(), tx.getBalanceAfter(), tx.getCreatedAt()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @AuthenticationPrincipal String userId,
            @RequestBody WithdrawRequest request) {
        try {
            BigDecimal amount = new BigDecimal(request.amount().toString());
            UUID orderRefId = request.orderReferenceId() != null
                    ? UUID.fromString(request.orderReferenceId().toString())
                    : null;

            var tx = transactionService.withdraw(UUID.fromString(userId), amount, orderRefId);
            return ResponseEntity.ok(new TransactionHistoryResponse(
                    tx.getId(), tx.getType(), tx.getAmount(),
                    tx.getStatus(), tx.getBalanceAfter(), tx.getCreatedAt()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TransactionHistoryResponse>> getHistory(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Transaction> txPage = transactionService.getHistory(
                UUID.fromString(userId), pageable);

        Page<TransactionHistoryResponse> responsePage = txPage.map(tx -> new TransactionHistoryResponse(
                tx.getId(), tx.getType(), tx.getAmount(),
                tx.getStatus(), tx.getBalanceAfter(), tx.getCreatedAt()));

        return ResponseEntity.ok(responsePage);
    }
}
