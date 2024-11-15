package com.nexters.goalpanzi.exception;

public class ForbiddenException extends BaseException {

    public ForbiddenException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
