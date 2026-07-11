package com.abc.auth.service.impl;

import com.abc.auth.dto.request.RefreshTokenRequest;
import com.abc.auth.dto.response.TokenResponse;
import com.abc.auth.model.UserSession;
import com.abc.auth.repository.UserSessionRepository;
import com.abc.auth.security.config.JwtProperties;
import com.abc.auth.security.constants.SecurityConstants;
import com.abc.auth.security.jwt.JwtService;
import com.abc.auth.service.RefreshTokenApiService;
import com.abc.auth.service.RefreshTokenService;
import com.abc.auth.service.SessionService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenApiServiceImpl implements RefreshTokenApiService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final UserSessionRepository userSessionRepository;
    private final JwtProperties jwtProperties;

    @Override
    public TokenResponse refresh(RefreshTokenRequest request){

        String refreshToken = request.getRefreshToken();
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token.");
        }

        String tokenType = jwtService.extractTokenType(refreshToken);

        if (!SecurityConstants.REFRESH_TOKEN_TYPE.equals(tokenType)) {
            throw new IllegalArgumentException("Only refresh tokens are allowed.");
        }

        //If JWT Valid Extract Claims
        Claims claims = jwtService.extractClaims(refreshToken);

        Long userId = claims.get(
                SecurityConstants.USER_ID_CLAIM,
                Long.class
        );

        UUID sessionId = UUID.fromString(
                claims.get(
                        SecurityConstants.SESSION_ID_CLAIM,
                        String.class
                )
        );

        UserSession session = sessionService.getActiveSession(sessionId);
        //1. Sliding Expiry
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Session has expired.");
        }

         //2. Absolute Expiry
        if (session.getAbsoluteExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Session has reached its maximum lifetime.");
        }

        //Verify UserId
        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Invalid session.");
        }

        //Verify Refresh Token Hash
        if (!refreshTokenService.matchesRefreshToken(
                refreshToken,
                session.getRefreshTokenHash()
        )) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

      //  Generate New Refresh Token
        String newRefreshToken =
                jwtService.generateRefreshToken(
                        session.getUser().getId(),
                        session.getUser().getEmail(),
                        session.getSessionId()
                );

        //Hash New Refresh Token
        String newRefreshTokenHash =
                refreshTokenService.hashRefreshToken(newRefreshToken);

      //  Generate New Access Token
        String newAccessToken =
                jwtService.generateAccessToken(
                        session.getUser().getId(),
                        session.getUser().getEmail()
                );

        //Update Session
        session.setRefreshTokenHash(newRefreshTokenHash);

        session.setRefreshCount(session.getRefreshCount() + 1);

        session.setLastUsedAt(LocalDateTime.now());

        session.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );

        userSessionRepository.save(session);

        //Return Response
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(SecurityConstants.TOKEN_TYPE)
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

}