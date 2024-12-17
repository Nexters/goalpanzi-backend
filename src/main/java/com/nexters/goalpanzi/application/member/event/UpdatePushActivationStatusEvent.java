package com.nexters.goalpanzi.application.member.event;

public record UpdatePushActivationStatusEvent(
        Long memberId,
        Long deviceId,
        Boolean isPushActivated,
        String deviceToken
) {
}
