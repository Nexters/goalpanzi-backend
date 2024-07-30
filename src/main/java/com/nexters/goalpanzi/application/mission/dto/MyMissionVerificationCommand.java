package com.nexters.goalpanzi.application.mission.dto;

public record MyMissionVerificationCommand(
        Long memberId,
        Long missionId,
        Integer number
) {
}
