package com.abc.auth.service.impl;

import com.abc.auth.dto.response.SessionResponse;
import com.abc.auth.model.User;
import com.abc.auth.model.UserSession;
import com.abc.auth.service.SessionQueryService;
import com.abc.auth.service.SessionService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionQueryServiceImpl implements SessionQueryService {

    private final SessionService sessionService;

    @Override
    public List<SessionResponse> getActiveSessions(User user) {

        List<UserSession> sessions =
                sessionService.getActiveSessions(user);

        return sessions.stream()
                .map(this::toResponse)
                .toList();
    }

    private SessionResponse toResponse(UserSession session) {

        return SessionResponse.builder()
                .sessionId(session.getSessionId())
                .deviceName(session.getDeviceName())
                .browser(session.getBrowser())
                .operatingSystem(session.getOperatingSystem())
                .ipAddress(session.getIpAddress())
                .createdAt(session.getCreatedAt())
                .lastUsedAt(session.getLastUsedAt())
                .build();
    }
}
