package com.abc.auth.service;

import com.abc.auth.model.User;

public interface LogoutService {
    void logout(String accessToken);

    void logoutAll(User user);
}
