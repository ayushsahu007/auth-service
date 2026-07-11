package com.abc.auth.service;

import com.abc.auth.model.User;

import java.util.UUID;

public interface LogoutService {
    void logout(String accessToken);

    void logoutAll(User user);

    void logoutSession(
            User user,
            UUID sessionId
    );
}
