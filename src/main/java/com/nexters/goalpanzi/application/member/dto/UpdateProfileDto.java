package com.nexters.goalpanzi.application.member.dto;

import com.nexters.goalpanzi.domain.member.CharacterType;

public record UpdateProfileDto(
        Long memberId,
        String nickname,
        CharacterType characterType
) {
}