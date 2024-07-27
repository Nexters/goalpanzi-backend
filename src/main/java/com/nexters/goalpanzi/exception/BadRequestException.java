package com.nexters.goalpanzi.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
    
    public BadRequestException(final String message) {
        super(message);
    }
}
