package com.EjadaIntern.inventory_service.application.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.inventory_service.application.dto.StockItemRequest;
import com.EjadaIntern.inventory_service.application.dto.StockReleaseRequest;
import com.EjadaIntern.inventory_service.application.dto.StockReservationRequest;
import com.EjadaIntern.inventory_service.application.dto.StockSaleRequest;
import com.EjadaIntern.inventory_service.application.service.StockTransactionService;
import com.EjadaIntern.inventory_service.domain.model.StockTransaction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final StockTransactionService stockService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateStock(@RequestBody List<StockItemRequest> items) {
        stockService.validateStockAvailability(items);
        return ResponseEntity.ok(Map.of("valid", true));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveStock(@RequestBody StockReservationRequest request) {
        stockService.reserveStock(request.productId(), request.quantity(), request.orderId());
        return ResponseEntity.ok(Map.of("status", "RESERVED"));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSale(@RequestBody StockSaleRequest request) {
        StockTransaction tx = stockService.confirmSale(
                request.productId(), request.quantity(), request.orderId());
        return ResponseEntity.ok(Map.of("transactionId", tx.getId(), "status", "CONFIRMED"));
    }

    @PostMapping("/release")
    public ResponseEntity<?> releaseStock(@RequestBody StockReleaseRequest request) {
        stockService.releaseReservedStock(request.productId(), request.quantity(), request.orderId());
        return ResponseEntity.ok(Map.of("status", "RELEASED"));
    }
}
