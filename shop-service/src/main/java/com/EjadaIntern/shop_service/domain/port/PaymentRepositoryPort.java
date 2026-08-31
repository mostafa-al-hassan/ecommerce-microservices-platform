package com.EjadaIntern.shop_service.domain.port;

import java.util.Optional;
import java.util.UUID;

import com.EjadaIntern.shop_service.domain.model.Payment;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);

    Optional<Payment> findById(UUID paymentId);

    Optional<Payment> findByOrderId(UUID orderId);
}
