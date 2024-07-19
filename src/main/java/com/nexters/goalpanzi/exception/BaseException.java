package com.nexters.goalpanzi.exception;

public class BaseException extends RuntimeException {

    public BaseException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public BaseException(final String message) {
        super(message);
    }
}
