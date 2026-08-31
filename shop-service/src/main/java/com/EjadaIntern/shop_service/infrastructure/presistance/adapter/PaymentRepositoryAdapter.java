package com.EjadaIntern.shop_service.infrastructure.presistance.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.EjadaIntern.shop_service.domain.model.Payment;
import com.EjadaIntern.shop_service.domain.port.PaymentRepositoryPort;
import com.EjadaIntern.shop_service.infrastructure.presistance.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentRepository paymentRepo;

    @Override
    public Payment save(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentRepo.findById(paymentId);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentRepo.findByOrderId(orderId);
    }
}
