package com.nexters.goalpanzi.exception;

public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public AlreadyExistsException(final String message) {
        super(message);
    }
}
