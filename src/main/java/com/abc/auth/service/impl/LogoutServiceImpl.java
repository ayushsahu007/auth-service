package com.abc.auth.service.impl;

import com.abc.auth.model.User;
import com.abc.auth.security.jwt.JwtService;
import com.abc.auth.service.LogoutService;
import com.abc.auth.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutServiceImpl implements LogoutService {


    private final JwtService jwtService;
    private final SessionService sessionService;

    @Override
    public void logout(String accessToken) {
        UUID sessionId =
                jwtService.extractSessionId(accessToken);

        sessionService.revokeSession(sessionId);
    }

    @Override
    public void logoutAll(User user) {

        sessionService.revokeAllSessions(user);

    }
}
