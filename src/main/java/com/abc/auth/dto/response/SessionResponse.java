package com.abc.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SessionResponse {

    private UUID sessionId;

    private String deviceName;

    private String browser;

    private String operatingSystem;

    private String ipAddress;

    private LocalDateTime createdAt;

    private LocalDateTime lastUsedAt;
}
