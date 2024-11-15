package com.nexters.goalpanzi.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(final ErrorCode errorCode, final Object... args) {
        super(errorCode, errorCode.getMessage(args));
    }
}
