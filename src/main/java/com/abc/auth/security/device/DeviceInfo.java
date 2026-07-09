package com.abc.auth.security.device;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceInfo {

    private String ipAddress;

    private String userAgent;

    private String deviceName;

    private String browser;

    private String operatingSystem;

}