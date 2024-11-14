package com.nexters.goalpanzi.application.auth.dto.request;

public record ReissueTokenCommand(
        Long memberId,
        String refreshToken
) {
}
