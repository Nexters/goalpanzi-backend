package com.nexters.goalpanzi.application.mission.dto;

public record CreateMissionVerificationCommand(
        Long memberId,
        Long missionId,
        String imageUrl
) {
}
