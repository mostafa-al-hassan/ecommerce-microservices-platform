package com.EjadaIntern.microservices.wallet.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circuitBreaker")
public class CircuitBreakerTestController {

    @GetMapping("/test")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("something something, not protected");
    }
}
