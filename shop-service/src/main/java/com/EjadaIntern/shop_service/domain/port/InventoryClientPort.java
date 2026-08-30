package com.EjadaIntern.shop_service.domain.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.EjadaIntern.shop_service.domain.dto.ProductDTO;
import com.EjadaIntern.shop_service.domain.dto.StockItemRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReleaseRequest;
import com.EjadaIntern.shop_service.domain.dto.StockReservationRequest;
import com.EjadaIntern.shop_service.domain.dto.StockSaleRequest;

public interface InventoryClientPort {
    void validateStock(List<StockItemRequest> items);

    void reserveStock(StockReservationRequest request);

    void confirmSale(StockSaleRequest request);

    void releaseStock(StockReleaseRequest request);

    ProductDTO getProductById(UUID productId);

    BigDecimal getProductPrice(UUID productId);

    UUID getSellerIdByProductId(UUID productId);
}
