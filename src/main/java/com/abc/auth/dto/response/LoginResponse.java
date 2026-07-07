package com.abc.auth.dto.response;

import lombok.*;


@Getter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String message;

    private long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String accessToken;

    private String tokenType;

    private long expiresIn;

}
