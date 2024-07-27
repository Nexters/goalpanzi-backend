package com.nexters.goalpanzi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorCode {
    // AUTH
    INVALID_APPLE_TOKEN("애플 토큰 검증에 실패하였습니다."),
    EXPIRED_APPLE_TOKEN("애플 토큰이 만료되었습니다."),

    INVALID_TOKEN("서버 토큰 검증에 실패하였습니다."),
    INVALID_REFRESH_TOKEN("refresh 토큰이 갱신되어 더 이상 유효하지 않은 refresh 토큰입니다."),
    EXPIRED_REFRESH_TOKEN("만료된 refresh 토큰입니다."),

    // MEMBER
    NOT_FOUND_MEMBER("존재하지 않는 회원입니다"),
    ALREADY_EXIST_NICKNAME("이미 존재하는 회원 닉네임입니다"),

    // MISSION VERIFICATION
    NOT_FOUND_VERIFICATION("존재하지 않는 미션 인증입니다."),
    DUPLICATE_VERIFICATION("이미 인증한 미션이므로 더 이상 인증할 수 없습니다."),
    ALREADY_COMPLETED_MISSION("이미 완료된 미션이므로 더 이상 인증할 수 없습니다.");
    private String message;
}
