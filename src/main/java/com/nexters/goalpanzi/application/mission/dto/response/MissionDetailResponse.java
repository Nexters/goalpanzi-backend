package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record MissionDetailResponse(
        @Schema(description = "미션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionId,
        @Schema(description = "회원(방장) ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long hostMemberId,
        @Schema(description = "목표 행동", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(description = "미션 시작 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime missionStartDate,
        @Schema(description = "미션 종료 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime missionEndDate,
        @Schema(description = "인증 업로드 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        TimeOfDay timeOfDay,
        @Schema(description = "경쟁 빈도", requiredMode = Schema.RequiredMode.REQUIRED)
        List<DayOfWeek> missionDays,
        @Schema(description = "보드칸 개수", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer boardCount,
        @Schema(description = "초대 코드", requiredMode = Schema.RequiredMode.REQUIRED)
        String invitationCode
) {

    public static MissionDetailResponse from(final Mission mission) {
        return new MissionDetailResponse(
                mission.getId(),
                mission.getHostMemberId(),
                mission.getDescription(),
                mission.getMissionStartDate(),
                mission.getMissionEndDate(),
                TimeOfDay.of(mission.getUploadStartTime(), mission.getUploadEndTime()),
                mission.getMissionDays(),
                mission.getBoardCount(),
                mission.getInvitationCode().getCode()
        );
    }
}