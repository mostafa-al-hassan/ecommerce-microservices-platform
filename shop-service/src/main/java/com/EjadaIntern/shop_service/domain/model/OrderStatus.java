package com.EjadaIntern.shop_service.domain.model;

public enum OrderStatus {
    PENDING,
    PAYMENT_INITIATED, // in case of wallet didn't respond it is good to know if the payment is
                       // initiated and hence we may avoid the double charge problem and avoid
                       // dangerous retries
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    REFUNDED
}
