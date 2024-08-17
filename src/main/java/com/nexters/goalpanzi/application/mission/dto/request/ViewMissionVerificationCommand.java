package com.nexters.goalpanzi.application.mission.dto.request;

public record ViewMissionVerificationCommand(
        Long missionVerificationId,
        Long memberId
) {
}
