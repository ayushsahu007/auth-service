package com.abc.auth.controller;

import com.abc.auth.dto.request.RefreshTokenRequest;
import com.abc.auth.dto.request.RegisterRequest;
import com.abc.auth.dto.response.*;
import com.abc.auth.dto.request.LoginRequest;
import com.abc.auth.security.constants.SecurityConstants;
import com.abc.auth.security.user.CustomUserDetails;
import com.abc.auth.service.AuthService;
import com.abc.auth.service.LogoutService;
import com.abc.auth.service.RefreshTokenApiService;
import com.abc.auth.service.SessionQueryService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenApiService refreshTokenApiService;
    private final LogoutService logoutService;
    private final SessionQueryService sessionQueryService;

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

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(

            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorizationHeader

    ) {

        String accessToken =
                authorizationHeader.substring(
                        SecurityConstants.BEARER_PREFIX.length()
                );

        logoutService.logout(accessToken);

        return ResponseEntity.ok(
                new LogoutResponse("Logout successful.")
        );
    }
    @PostMapping("/logout-all")
    public ResponseEntity<LogoutResponse> logoutAll() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        logoutService.logoutAll(userDetails.getUser());

        return ResponseEntity.ok(
                new LogoutResponse("All sessions logged out successfully.")
        );
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponse>> getActiveSessions(
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        List<SessionResponse> sessions =
                sessionQueryService.getActiveSessions(userDetails.getUser());

        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<LogoutResponse> logoutSession(
            @PathVariable UUID sessionId,
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        logoutService.logoutSession(
                userDetails.getUser(),
                sessionId
        );

        return ResponseEntity.ok(
                new LogoutResponse("Session logged out successfully.")
        );
    }

}
