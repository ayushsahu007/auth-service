package com.abc.auth.service;

import com.abc.auth.model.UserSession;

import java.util.UUID;

public interface RefreshTokenService {


    String generateRefreshToken(UserSession session);

    String hashRefreshToken(String refreshToken);

    boolean matchesRefreshToken(
            String refreshToken,
            String storedHash
    );

}