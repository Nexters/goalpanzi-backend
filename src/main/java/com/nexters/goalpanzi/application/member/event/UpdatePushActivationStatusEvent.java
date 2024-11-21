package com.nexters.goalpanzi.application.member.event;

public record UpdatePushActivationStatusEvent(
        Long memberId,
        String deviceToken,
        Boolean isPushActivated
) {
}
