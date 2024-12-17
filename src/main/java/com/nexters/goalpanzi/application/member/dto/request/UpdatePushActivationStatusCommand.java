package com.nexters.goalpanzi.application.member.dto.request;

public record UpdatePushActivationStatusCommand(
        Long memberId,
        String deviceIdentifier,
        Boolean pushActivationStatus
) {
}
