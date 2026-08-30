package com.EjadaIntern.shop_service.application.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.shop_service.application.dto.OrderResponse;
import com.EjadaIntern.shop_service.application.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        OrderResponse response = orderService.createOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> refundOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        orderService.refundOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    // private helpers

    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new IllegalStateException("Authenticated user ID not available in security context");
        }
        try {
            return UUID.fromString(userDetails.getUsername());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("User principal is not a valid UUID: " + userDetails.getUsername(), e);
        }
    }
}
