package com.nexters.goalpanzi.application.device.event;

public record UpdatePushActivationStatusEvent(
        Long memberId,
        Long deviceId,
        Boolean isPushActivated,
        String deviceToken
) {
}
