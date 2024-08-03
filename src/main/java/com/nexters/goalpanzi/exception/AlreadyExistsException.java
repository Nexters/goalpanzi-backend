package com.nexters.goalpanzi.exception;

public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public AlreadyExistsException(final ErrorCode errorCode, final Object... args) {
        super(errorCode, args);
    }

    public AlreadyExistsException(final String message) {
        super(message);
    }
}