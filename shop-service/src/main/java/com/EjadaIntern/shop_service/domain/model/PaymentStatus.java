package com.EjadaIntern.shop_service.domain.model;

public enum PaymentStatus {
    PENDING,        // Payment record created, not yet sent to Wallet
    INITIATED,      // Sent to Wallet, awaiting response (safety gate)
    COMPLETED,      // Wallet confirmed successful transfer
    FAILED,         // Wallet rejected or timed out
    REFUND_PENDING, // Refund requested, awaiting Wallet confirmation
    REFUNDED        // Wallet confirmed refund processed
}
