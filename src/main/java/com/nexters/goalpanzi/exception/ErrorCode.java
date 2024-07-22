package com.nexters.goalpanzi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_APPLE_TOKEN("애플 토큰 검증에 실패하였습니다."),
    EXPIRED_APPLE_TOKEN("애플 토큰이 만료되었습니다."),

    INVALID_TOKEN("토큰 검증에 실패하였습니다."),
    INVALID_REFRESH_TOKEN("refresh 토큰이 갱신되어 더 이상 유효하지 않은 refresh 토큰입니다."),
    EXPIRED_REFRESH_TOKEN("만료된 refresh 토큰입니다.");

    private String message;
}
