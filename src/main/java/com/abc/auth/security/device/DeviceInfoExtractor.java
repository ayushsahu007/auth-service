package com.abc.auth.security.device;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceInfoExtractor {

    public DeviceInfo extract(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        return DeviceInfo.builder()
                .ipAddress(extractClientIp(request))
                .userAgent(userAgent)
                .deviceName("Unknown Device")
                .browser("Unknown")
                .operatingSystem("Unknown")
                .build();
    }

    private String extractClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

}