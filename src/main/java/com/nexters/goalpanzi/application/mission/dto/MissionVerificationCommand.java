package com.nexters.goalpanzi.application.mission.dto;

public record MissionVerificationCommand(
        Long memberId,
        Long missionId
) {
}
