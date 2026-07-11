package com.abc.auth.service;

import com.abc.auth.model.User;
import com.abc.auth.model.UserSession;

import java.util.List;
import java.util.UUID;
public interface SessionService {

//    UserSession updateRefreshToken(
//            UserSession session,
//            String refreshTokenHash
//    );

    UserSession createSession(
            User user,
            UUID sessionId,
            String refreshTokenHash
    );

    UserSession getActiveSession(UUID sessionId);

    List<UserSession> getActiveSessions(User user);

    UserSession validateActiveSession(UUID sessionId);

    void revokeSession(UUID sessionId);

    void revokeAllSessions(User user);

}
