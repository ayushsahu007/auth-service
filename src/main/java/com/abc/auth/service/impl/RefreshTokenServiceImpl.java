package com.abc.auth.service.impl;

import com.abc.auth.model.UserSession;
import com.abc.auth.security.jwt.JwtService;
import com.abc.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtService jwtService;

    @Override
    public String generateRefreshToken(UserSession session) {

        return jwtService.generateRefreshToken(
                session.getUser().getId(),
                session.getUser().getEmail(),
                session.getSessionId()
        );
    }

    @Override
    public String hashRefreshToken(String refreshToken) {

        try {

            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    messageDigest.digest(
                            refreshToken.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "Unable to hash refresh token",
                    ex
            );
        }
    }

    @Override
    public boolean matchesRefreshToken(
            String refreshToken,
            String storedHash
    ) {

        String hashedToken = hashRefreshToken(refreshToken);

        return MessageDigest.isEqual(
                hashedToken.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

}