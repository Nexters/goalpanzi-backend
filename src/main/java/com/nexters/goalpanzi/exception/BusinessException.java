package com.nexters.goalpanzi.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
