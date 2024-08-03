package com.nexters.goalpanzi.application.mission.dto.request;

public record MyMissionVerificationCommand(
        Long memberId,
        Long missionId,
        Integer number
) {
}
