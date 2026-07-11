package com.abc.auth.service.impl;

import com.abc.auth.model.User;
import com.abc.auth.model.UserSession;
import com.abc.auth.model.enums.SessionStatus;
import com.abc.auth.repository.UserSessionRepository;
import com.abc.auth.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private static final int SLIDING_EXPIRY_DAYS = 30;
    private static final int ABSOLUTE_EXPIRY_DAYS = 90;

    private final UserSessionRepository userSessionRepository;

    @Override
    public UserSession createSession(
            User user,
            UUID sessionId,
            String refreshTokenHash) {

        LocalDateTime now = LocalDateTime.now();

        UserSession session = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .refreshTokenHash(refreshTokenHash)
                .status(SessionStatus.ACTIVE)
                .refreshCount(0)
                .lastUsedAt(now)
                .expiresAt(now.plusDays(SLIDING_EXPIRY_DAYS))
                .absoluteExpiresAt(now.plusDays(ABSOLUTE_EXPIRY_DAYS))
                .build();

        return userSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSession getActiveSession(UUID sessionId) {

        return userSessionRepository
                .findBySessionIdAndStatus(sessionId, SessionStatus.ACTIVE)
                .orElseThrow(() ->
                        new EntityNotFoundException("Active session not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(User user) {

        return userSessionRepository.findAllByUserAndStatus(
                user,
                SessionStatus.ACTIVE
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserSession validateActiveSession(UUID sessionId) {

        UserSession session = userSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Session not found."));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new EntityNotFoundException("Session is not active.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (session.getExpiresAt().isBefore(now)) {
            throw new EntityNotFoundException("Session expired.");
        }

        if (session.getAbsoluteExpiresAt().isBefore(now)) {
            throw new EntityNotFoundException("Session expired.");
        }

        return session;
    }

    @Override
    public void revokeSession(UUID sessionId) {

        UserSession session = getActiveSession(sessionId);

        session.setStatus(SessionStatus.REVOKED);
        session.setRevokedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    @Override
    public void revokeAllSessions(User user) {

        List<UserSession> sessions =
                userSessionRepository.findAllByUserAndStatus(
                        user,
                        SessionStatus.ACTIVE
                );

        LocalDateTime now = LocalDateTime.now();

        sessions.forEach(session -> {
            session.setStatus(SessionStatus.REVOKED);
            session.setRevokedAt(now);
        });

        userSessionRepository.saveAll(sessions);
    }
}