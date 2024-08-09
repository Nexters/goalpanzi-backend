package com.nexters.goalpanzi.application.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MissionBoardsResponse(
        @Schema(description = "미션 보드칸 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionBoardResponse> missionBoards
) {
}
