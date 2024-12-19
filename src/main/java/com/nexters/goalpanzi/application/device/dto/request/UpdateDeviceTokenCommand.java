package com.nexters.goalpanzi.application.device.dto.request;

import com.nexters.goalpanzi.domain.device.OsType;

public record UpdateDeviceTokenCommand(
        Long memberId,
        String deviceIdentifier,
        String deviceToken,
        OsType osType
) {
}
