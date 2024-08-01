package com.nexters.goalpanzi.application.member.dto.request;

import com.nexters.goalpanzi.domain.member.CharacterType;

public record UpdateProfileCommand(
        Long memberId,
        String nickname,
        CharacterType characterType
) {
}