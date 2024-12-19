package com.nexters.goalpanzi.application.device.dto.request;

public record UpdatePushActivationStatusCommand(
        Long memberId,
        String deviceIdentifier,
        Boolean pushActivationStatus
) {
}
