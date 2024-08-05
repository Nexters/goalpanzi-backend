package com.nexters.goalpanzi.application.mission.event;

public record CreateMissionEvent(
        Long memberId,
        String invitationCode
) {
}
