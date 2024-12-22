package com.nexters.goalpanzi.application.mission.event;

public record DeleteMissionEvent(
        Long memberId,
        Long missionId
) {
}
