package com.nexters.goalpanzi.application.mission.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateMissionVerificationCommand(
        Long memberId,
        Long missionId,
        MultipartFile imageFile
) {
}
