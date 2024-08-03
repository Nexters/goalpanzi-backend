package com.nexters.goalpanzi.presentation.mission.dto;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateMissionVerificationRequest(
        @Schema(description = "인증 이미지 파일", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull MultipartFile imageFile
) {

    public CreateMissionVerificationCommand toServiceDto(final Long memberId, final Long missionId) {
        return new CreateMissionVerificationCommand(memberId, missionId, imageFile);
    }
}
