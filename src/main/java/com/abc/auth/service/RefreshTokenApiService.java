package com.abc.auth.service;

import com.abc.auth.dto.request.RefreshTokenRequest;
import com.abc.auth.dto.response.TokenResponse;

public interface RefreshTokenApiService {
    TokenResponse refresh(RefreshTokenRequest request);
}
