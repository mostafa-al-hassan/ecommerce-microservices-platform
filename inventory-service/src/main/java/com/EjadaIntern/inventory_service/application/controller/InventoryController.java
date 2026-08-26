package com.EjadaIntern.inventory_service.application.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.inventory_service.infrastructure.client.WalletInternalClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final WalletInternalClient walletInternalClient;

    @GetMapping("/ping")
    public ResponseEntity<?> transfer() {
        try {
            // This call will automatically use ServiceTokenManager and FeignConfig
            Map<String, String> response = walletInternalClient.pingWallet();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

    }
}
