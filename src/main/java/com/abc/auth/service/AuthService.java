package com.abc.auth.service;

import com.abc.auth.dto.request.RegisterRequest;
import com.abc.auth.dto.response.RegisterResponse;
import com.abc.auth.dto.request.LoginRequest;
import com.abc.auth.dto.response.LoginResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

}