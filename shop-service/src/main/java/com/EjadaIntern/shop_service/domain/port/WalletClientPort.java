package com.EjadaIntern.shop_service.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

import com.EjadaIntern.shop_service.domain.dto.RefundRequest;
import com.EjadaIntern.shop_service.domain.dto.RefundResponse;
import com.EjadaIntern.shop_service.domain.dto.TransferRequest;
import com.EjadaIntern.shop_service.domain.dto.TransferResponse;

public interface WalletClientPort {
    TransferResponse transferFunds(TransferRequest request);

    BigDecimal getBalance(UUID userId);

    RefundResponse refundTransaction(RefundRequest request);
}
