package com.nexters.goalpanzi.application.device.event;

public record UpdateDeviceTokenEvent(
        Long memberId,
        Long deviceId,
        String deprecatedDeviceToken
) {

    public boolean isTokenDeprecated() {
        return deprecatedDeviceToken != null;
    }
}
