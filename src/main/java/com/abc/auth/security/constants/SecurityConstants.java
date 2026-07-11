package com.abc.auth.security.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String TOKEN_TYPE = "Bearer";

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String USER_ID_CLAIM = "userId";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String SESSION_ID_CLAIM = "sid";

    public static final String TOKEN_ID_CLAIM = "jti";

    public static final String TOKEN_TYPE_CLAIM = "token_type";

    public static final String ACCESS_TOKEN_TYPE = "ACCESS";

    public static final String REFRESH_TOKEN_TYPE = "REFRESH";
}
