package com.nexters.goalpanzi.exception;

public class BaseException extends RuntimeException {

    public BaseException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public BaseException(final ErrorCode errorCode, final Object... args) {
        super(errorCode.getMessage(args));
    }

    public BaseException(final String message) {
        super(message);
    }
}
