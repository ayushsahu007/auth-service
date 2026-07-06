package com.abc.auth.service;

import com.abc.auth.dto.RegisterRequest;
import com.abc.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

}