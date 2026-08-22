package com.EjadaIntern.microservices.wallet.application.dto;

import java.util.*;

import jakarta.validation.constraints.NotNull;

public record RefundRequest(
    @NotNull UUID originalTransactionId,
    @NotNull UUID orderReferenceId
) {
    
}
