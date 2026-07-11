package com.abc.auth.service.impl;

import com.abc.auth.model.User;
import com.abc.auth.model.UserSession;
import com.abc.auth.repository.UserSessionRepository;
import com.abc.auth.security.jwt.JwtService;
import com.abc.auth.service.LogoutService;
import com.abc.auth.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserSessionRepository userSessionRepository;

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

    @Override
    public void logoutSession(User user, UUID sessionId) {

        UserSession session = userSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Session not found."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SecurityException(
                    "You are not allowed to logout this session."
            );
        }

        sessionService.revokeSession(sessionId);
    }
}
