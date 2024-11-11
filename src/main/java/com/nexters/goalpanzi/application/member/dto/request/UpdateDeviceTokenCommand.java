package com.nexters.goalpanzi.application.member.dto.request;

public record UpdateDeviceTokenCommand(
        Long memberId,
        String deviceToken
) {
}
