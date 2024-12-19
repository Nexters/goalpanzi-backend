package com.nexters.goalpanzi.application.auth.event;

public record LoginEvent(
        Long memberId,
        String deviceIdentifier
) {
}
