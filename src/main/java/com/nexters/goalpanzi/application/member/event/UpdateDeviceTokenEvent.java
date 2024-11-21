package com.nexters.goalpanzi.application.member.event;

public record UpdateDeviceTokenEvent(
        Long memberId,
        String deprecatedDeviceToken,
        String deviceToken
) {
}
