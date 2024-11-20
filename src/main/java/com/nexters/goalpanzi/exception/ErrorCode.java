package com.nexters.goalpanzi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorCode {
    // GENERAL
    INTERNAL_SERVER_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    BAD_REQUEST("잘못된 요청입니다."),
    UNAUTHORIZED("권한이 없습니다."),
    RESOURCE_NOT_FOUND("리소스를 찾을 수 없습니다."),

    // OAUTH
    INVALID_APPLE_TOKEN("애플 토큰 검증에 실패하였습니다."),
    EXPIRED_APPLE_TOKEN("애플 토큰이 만료되었습니다."),
    CAN_NOT_CREATE_PUBLIC_KEY("응답 받은 Apple Public Key로 PublicKey를 생성할 수 없습니다."),
    INVALID_APPLE_TOKEN_FORMAT("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."),

    INVALID_TOKEN("서버 토큰 검증에 실패하였습니다."),
    INVALID_REFRESH_TOKEN("refresh 토큰이 갱신되어 더 이상 유효하지 않은 refresh 토큰입니다."),
    EXPIRED_REFRESH_TOKEN("만료된 refresh 토큰입니다."),

    // MEMBER
    NOT_FOUND_MEMBER("존재하지 않는 회원입니다. [%s]"),
    ALREADY_EXIST_NICKNAME("이미 존재하는 회원 닉네임입니다. [%s]"),

    // MISSION
    NOT_FOUND_MISSION("존재하지 않는 미션입니다. [%s]"),
    INVALID_INVITATION_CODE("초대코드가 올바르지 않습니다."),
    INVALID_UPLOAD_TIME_OF_DAY("올바르지 않은 미션 인증 업로드 시간대입니다."),
    CANNOT_DELETE_MISSION("미션 삭제 권한이 없습니다."),
    UNKNOWN_MISSION("정의되지 않은 미션상태입니다. [missionId=%s, memberId=%s]"),

    // MISSION MEMBER
    ALREADY_EXISTS_MISSION_MEMBER("이미 참여한 미션입니다. [%s]"),
    NOT_JOINED_MISSION_MEMBER("해당 미션에 참여하지 않았습니다."),
    EXCEED_MAX_PERSONNEL("미션 최대 인원을 초과했습니다. [%s]"),
    CAN_NOT_JOIN_MISSION("미션 참여가능 날짜가 아닙니다."),

    // MISSION VERIFICATION
    NOT_FOUND_VERIFICATION("존재하지 않는 미션 인증입니다."),
    DUPLICATE_VERIFICATION("이미 인증한 미션이므로 더 이상 인증할 수 없습니다."),
    ALREADY_COMPLETED_MISSION("이미 완료된 미션이므로 더 이상 인증할 수 없습니다."),
    NOT_VERIFICATION_PERIOD("인증 기간이 아니므로 인증할 수 없습니다."),
    NOT_VERIFICATION_DAY("인증 일자가 아니므로 인증할 수 없습니다."),
    NOT_VERIFICATION_TIME("인증 시간대가 아니므로 인증할 수 없습니다."),

    // FILE UPLOAD
    INVALID_FILE("유효하지 않은 파일입니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다. [%s]"),

    // FIREBASE
    FAILED_TO_SEND_INDIVIDUAL_MESSAGE("개인 푸시 알림을 보내는 데 실패하였습니다. [%s]"),
    FAILED_TO_SEND_GROUP_MESSAGE("그룹 푸시 알림을 보내는 데 실패하였습니다. [%s]"),
    FAILED_TO_SUBSCRIBE_TO_TOPIC("토픽을 구독하는 데 실패하였습니다."),
    FAILED_TO_UNSUBSCRIBE_FROM_TOPIC("토픽을 구독 취소하는 데 실패하였습니다."),

    // ETC
    FAILED_TO_GENERATE_HASH("해시값을 생성하는 데 실패하였습니다."),
    FAILED_TO_ACQUIRE_REDISSON_LOCK("분산락을 획득하는 데 실패하였습니다."),
    ;

    private String message;

    public String getMessage(final Object... args) {
        return String.format(message, args);
    }
}
