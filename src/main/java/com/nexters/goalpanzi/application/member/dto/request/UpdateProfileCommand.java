package com.nexters.goalpanzi.application.member.dto.request;

import com.nexters.goalpanzi.domain.member.CharacterType;

import java.util.Optional;

public record UpdateProfileCommand(
        Long memberId,
        Optional<String> nickname,
        Optional<CharacterType> characterType
) {
}