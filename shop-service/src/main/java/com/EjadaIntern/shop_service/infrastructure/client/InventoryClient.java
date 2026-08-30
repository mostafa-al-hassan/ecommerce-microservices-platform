package com.EjadaIntern.shop_service.infrastructure.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.EjadaIntern.shop_service.domain.dto.ProductDTO;
import com.EjadaIntern.shop_service.domain.dto.StockItemRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReleaseRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReservationRequest;
import com.EjadaIntern.shop_service.domain.dto.StockSaleRequest;
import com.EjadaIntern.shop_service.domain.port.InventoryClientPort;

@FeignClient(name = "inventory-service")
public interface InventoryClient extends InventoryClientPort {

    @Override
    @PostMapping("/internal/inventory/validate")
    void validateStock(@RequestBody List<StockItemRequest> items);

    @Override
    @PostMapping("/internal/inventory/reserve")
    void reserveStock(@RequestBody StockReservationRequest request);

    @Override
    @PostMapping("/internal/inventory/confirm")
    void confirmSale(@RequestBody StockSaleRequest request);

    @Override
    @PostMapping("/internal/inventory/release")
    void releaseStock(@RequestBody StockReleaseRequest request);

    @Override
    @GetMapping("/internal/inventory/products/{productId}")
    ProductDTO getProductById(@PathVariable UUID productId);

    @Override
    @GetMapping("/internal/inventory/products/{productId}/price")
    BigDecimal getProductPrice(@PathVariable UUID productId);

    @Override
    @GetMapping("/internal/inventory/products/{productId}/seller")
    UUID getSellerIdByProductId(@PathVariable UUID productId);
}
