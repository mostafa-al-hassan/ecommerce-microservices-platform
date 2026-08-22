package com.EjadaIntern.microservices.wallet.application.controller;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EjadaIntern.microservices.wallet.application.dto.AuthResponse;
import com.EjadaIntern.microservices.wallet.application.dto.LoginRequest;
import com.EjadaIntern.microservices.wallet.application.dto.RegisterRequest;
import com.EjadaIntern.microservices.wallet.application.dto.UserResponse;
import com.EjadaIntern.microservices.wallet.application.service.UserLoginService;
import com.EjadaIntern.microservices.wallet.application.service.UserRegistrationService;
import com.EjadaIntern.microservices.wallet.domain.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRegistrationService registrationService;
    private final UserLoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = registrationService.register(request.email(), request.password(), request.firstName(),
                request.lastName(), request.role());

        return ResponseEntity.status(HttpStatus.SC_CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = loginService.login(request.email(), request.password());
        return ResponseEntity.ok(response);
    }

}
