package com.EjadaIntern.shop_service.application.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.shop_service.application.dto.AddToCartRequest;
import com.EjadaIntern.shop_service.application.dto.CartResponse;
import com.EjadaIntern.shop_service.application.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(cartService.getOrCreateActiveCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddToCartRequest request) {
        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID cartItemId) {
        UUID userId = extractUserId(userDetails);
        cartService.removeItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // helper method
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
