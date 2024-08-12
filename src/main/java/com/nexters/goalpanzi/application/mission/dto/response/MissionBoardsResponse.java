package com.nexters.goalpanzi.application.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MissionBoardsResponse(
        @Schema(description = "오늘 한 칸 전진한 멤버 수", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer progressCount,
        @Schema(description = "나의 꾸준함 순위", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer rank,
        @Schema(description = "미션 보드칸 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionBoardResponse> missionBoards
) {
}
