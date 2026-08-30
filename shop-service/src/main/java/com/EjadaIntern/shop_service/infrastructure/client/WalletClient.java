package com.EjadaIntern.shop_service.infrastructure.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.EjadaIntern.shop_service.domain.dto.RefundRequest;
import com.EjadaIntern.shop_service.domain.dto.RefundResponse;
import com.EjadaIntern.shop_service.domain.dto.TransferRequest;
import com.EjadaIntern.shop_service.domain.dto.TransferResponse;
import com.EjadaIntern.shop_service.domain.port.WalletClientPort;

@FeignClient(name = "wallet-service")
public interface WalletClient extends WalletClientPort {

    @Override
    @PostMapping("/internal/wallets/transfer")
    TransferResponse transferFunds(@RequestBody TransferRequest request);

    @Override
    @GetMapping("/internal/wallets/{userId}/balance")
    BigDecimal getBalance(@PathVariable UUID userId);

    @Override
    @PostMapping("/internal/wallets/refund")
    RefundResponse refundTransaction(@RequestBody RefundRequest request);
}
