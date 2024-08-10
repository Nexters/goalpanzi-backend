package com.nexters.goalpanzi.application.mission.dto.request;

import com.nexters.goalpanzi.domain.mission.BoardOrderBy;

public record MissionBoardQuery(
        Long memberId,
        Long missionId,
        BoardOrderBy orderBy
) {
}
