package com.nexters.goalpanzi.exception;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public UnauthorizedException(final String message) {
        super(message);
    }
}
