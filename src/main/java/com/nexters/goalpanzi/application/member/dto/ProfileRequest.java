package com.nexters.goalpanzi.application.member.dto;

import com.nexters.goalpanzi.domain.member.CharacterType;
import jakarta.validation.constraints.NotNull;

public record ProfileRequest(
        @NotNull
        String nickname,
        @NotNull
        CharacterType characterType
) {
}
