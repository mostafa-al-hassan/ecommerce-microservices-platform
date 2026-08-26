package com.EjadaIntern.inventory_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@FeignClient(name = "wallet-service")
public interface WalletInternalClient {

    /**
     * Calls the Wallet's internal ping endpoint.
     *
     */
    @GetMapping("/internal/wallets/ping")
    Map<String, String> pingWallet();
}
