package com.abc.auth.dto.response;

import jakarta.validation.constraints.AssertFalse;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {

    private String message;

    private long userId;

    private String firstName;

    private String lastName;

    private String email;


}
