package com.nexters.goalpanzi.presentation.member.dto;

import com.nexters.goalpanzi.application.member.dto.request.UpdateProfileCommand;
import com.nexters.goalpanzi.domain.member.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

public record UpdateProfileRequest(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Optional<String> nickname,
        @Schema(description = "장기말 타입", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Optional<CharacterType> characterType
) {

    public UpdateProfileRequest(final String nickname, final CharacterType characterType) {
        this(Optional.ofNullable(nickname), Optional.ofNullable(characterType));
    }

    public UpdateProfileCommand toServiceDto(Long memberId) {
        return new UpdateProfileCommand(
                memberId,
                nickname,
                characterType
        );
    }
}
