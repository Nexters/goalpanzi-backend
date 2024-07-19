package com.nexters.goalpanzi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_APPLE_TOKEN("애플 토큰 검증에 실패하였습니다."),
    EXPIRED_APPLE_TOKEN("애플 토큰이 만료되었습니다");

    private String message;
}
