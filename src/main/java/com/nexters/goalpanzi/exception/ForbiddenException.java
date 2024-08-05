package com.nexters.goalpanzi.exception;

public class ForbiddenException extends BaseException {

    public ForbiddenException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public ForbiddenException(final String message) {
        super(message);
    }
}
