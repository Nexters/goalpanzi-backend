package com.nexters.goalpanzi.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
