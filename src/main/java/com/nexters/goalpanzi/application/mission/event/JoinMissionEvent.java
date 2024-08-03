package com.nexters.goalpanzi.application.mission.event;

public record JoinMissionEvent(
        Long memberId,
        String invitationCode
) {
}
