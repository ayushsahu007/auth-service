package com.abc.auth.controller;

import com.abc.auth.dto.request.RefreshTokenRequest;
import com.abc.auth.dto.request.RegisterRequest;
import com.abc.auth.dto.response.RegisterResponse;
import com.abc.auth.dto.request.LoginRequest;
import com.abc.auth.dto.response.LoginResponse;
import com.abc.auth.dto.response.TokenResponse;
import com.abc.auth.service.AuthService;
import com.abc.auth.service.RefreshTokenApiService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenApiService refreshTokenApiService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        TokenResponse response =
                refreshTokenApiService.refresh(request);

        return ResponseEntity.ok(response);
    }

}
