package com.nexters.goalpanzi.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(final ErrorCode errorCode, final Object... args) {
        super(errorCode, args);
    }
}
