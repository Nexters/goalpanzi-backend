package com.nexters.goalpanzi.presentation.member.dto;

import com.nexters.goalpanzi.application.member.dto.request.UpdateProfileCommand;
import com.nexters.goalpanzi.domain.member.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateProfileRequest(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String nickname,
        @Schema(description = "장기말 타입", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        CharacterType characterType
) {

    public UpdateProfileCommand toServiceDto(Long memberId) {
        return new UpdateProfileCommand(
                memberId,
                nickname,
                characterType
        );
    }
}
