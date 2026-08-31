package com.EjadaIntern.microservices.wallet.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EjadaIntern.microservices.wallet.application.port.TransactionServicePort;
import com.EjadaIntern.microservices.wallet.domain.model.Transaction;
import com.EjadaIntern.microservices.wallet.domain.model.TransactionStatus;
import com.EjadaIntern.microservices.wallet.domain.model.TransactionType;
import com.EjadaIntern.microservices.wallet.domain.model.User;
import com.EjadaIntern.microservices.wallet.domain.model.Wallet;
import com.EjadaIntern.microservices.wallet.domain.port.TransactionRepositoryPort;
import com.EjadaIntern.microservices.wallet.domain.port.UserRepositoryPort;
import com.EjadaIntern.microservices.wallet.domain.port.WalletRepositoryPort;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionServicePort {

    private final TransactionRepositoryPort transactionRepo;
    private final WalletRepositoryPort walletRepo;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional
    @Override
    public Transaction deposit(UUID userId, BigDecimal amount, UUID orderReferenceId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet not found for user: " + userId));

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceAfter(newBalance)
                .status(TransactionStatus.COMPLETED)
                .orderReferenceId(orderReferenceId)
                .build();

        return transactionRepo.save(tx);
    }

    @Transactional
    @Override
    public Transaction withdraw(UUID userId, BigDecimal amount, UUID orderReferenceId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive");

        if (orderReferenceId != null) {
            List<Transaction> existingTxs = transactionRepo.findByOrderReferenceId(orderReferenceId);
            if (!existingTxs.isEmpty()) {
                // Return the first matching transaction for idempotency
                return existingTxs.get(0);
            }
        }

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet not found for user: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0)
            throw new IllegalStateException(
                    String.format("Insufficient funds. Available: %s, Requested: %s",
                            wallet.getBalance(), amount));

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .balanceAfter(newBalance)
                .status(TransactionStatus.COMPLETED)
                .orderReferenceId(orderReferenceId)
                .build();

        return transactionRepo.save(tx);
    }

    @Transactional
    @Override
    public Transaction transfer(UUID fromUserId, UUID toUserId, BigDecimal amount, UUID orderReferenceId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transfer amount must be positive");
        if (fromUserId.equals(toUserId))
            throw new IllegalArgumentException("Cannot transfer to self");

        Wallet sender = walletRepo.findByUserId(fromUserId)
                .orElseThrow(() -> new EntityNotFoundException("Sender wallet not found"));
        Wallet receiver = walletRepo.findByUserId(toUserId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver wallet not found"));

        if (sender.getBalance().compareTo(amount) < 0)
            throw new IllegalStateException(
                    String.format("Insufficient funds for transfer. Available: %s", sender.getBalance()));

        BigDecimal senderNewBalance = sender.getBalance().subtract(amount);
        BigDecimal receiverNewBalance = receiver.getBalance().add(amount);

        sender.setBalance(senderNewBalance);
        receiver.setBalance(receiverNewBalance);
        walletRepo.save(sender);
        walletRepo.save(receiver);

        Transaction senderTx = Transaction.builder()
                .wallet(sender)
                .type(TransactionType.TRANSFER)
                .amount(amount.negate())
                .balanceAfter(senderNewBalance)
                .status(TransactionStatus.COMPLETED)
                .orderReferenceId(orderReferenceId)
                .build();

        Transaction receiverTx = Transaction.builder()
                .wallet(receiver)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .balanceAfter(receiverNewBalance)
                .status(TransactionStatus.COMPLETED)
                .orderReferenceId(orderReferenceId)
                .build();

        transactionRepo.save(senderTx);
        return transactionRepo.save(receiverTx);
    }

    @Override
    public Page<Transaction> getHistory(UUID userId, Pageable pageable) {
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet not found for user: " + userId));
        return transactionRepo.findByWalletId(wallet.getId(), pageable);
    }

    @Override
    public BigDecimal getBalanceByUserId(UUID userId) {
        return walletRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"))
                .getBalance();
    }

    @Override
    @Transactional
    public Transaction refund(UUID originalTransactionId, UUID newOrderReferenceId) {
        Transaction original = transactionRepo.findById(originalTransactionId)
                .orElseThrow(() -> new EntityNotFoundException("Original transaction not found"));

        if (original.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund COMPLETED transactions");
        }

        User originalReceiver = userRepositoryPort.findByWalletId(original.getWallet().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for this wallet"));

        BigDecimal refundAmount;
        User originalSender;

        if (original.getType() == TransactionType.TRANSFER) {
            List<Transaction> relatedTxs = transactionRepo.findByOrderReferenceId(original.getOrderReferenceId());

            if (relatedTxs.isEmpty()) {
                throw new EntityNotFoundException(
                        "No related transactions found for order: " + original.getOrderReferenceId());
            }

            Transaction senderTx = relatedTxs.stream()
                    .filter(tx -> tx.getType() == TransactionType.TRANSFER)
                    .filter(tx -> tx.getAmount().compareTo(BigDecimal.ZERO) < 0) // Negative = sender side
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Matching sender transaction not found for order: " + original.getOrderReferenceId()));

            originalSender = userRepositoryPort.findByWalletId(senderTx.getWallet().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Original sender user not found"));

            refundAmount = original.getAmount();
        } else {
            throw new IllegalStateException("Cannot refund transaction of type: " + original.getType());
        }

        return transfer(
                originalReceiver.getId(),
                originalSender.getId(),
                refundAmount,
                newOrderReferenceId);
    }
}
