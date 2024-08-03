package com.nexters.goalpanzi.presentation.mission.dto;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionCommand;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMissionRequest(
        @Schema(description = "목표 행동", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(description = "미션 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime missionStartDate,

        @Schema(description = "미션 종료 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime missionEndDate,

        @Schema(description = "인증 업로드 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        TimeOfDay timeOfDay,

        @Schema(description = "경쟁 빈도", requiredMode = Schema.RequiredMode.REQUIRED)
        List<DayOfWeek> missionDays,

        @NotNull
        @Schema(description = "보드 칸 개수", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer boardCount
) {

    public CreateMissionCommand toServiceDto(final Long memberId) {
        return new CreateMissionCommand(
                memberId,
                description,
                missionStartDate,
                missionEndDate,
                timeOfDay,
                missionDays,
                boardCount
        );
    }
}
