package com.abc.auth.service;

import com.abc.auth.dto.response.SessionResponse;
import com.abc.auth.model.User;

import java.util.List;

public interface SessionQueryService {
    List<SessionResponse> getActiveSessions(User user);

}
