package com.nexters.goalpanzi.application.member.event;

public record UpdateDeviceTokenEvent(
        Long memberId,
        Long deviceId,
        String deprecatedDeviceToken
) {
}
