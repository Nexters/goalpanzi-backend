package com.nexters.goalpanzi.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(final ErrorCode errorCode, final Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public BaseException(final ErrorCode errorCode, final Exception e) {
        super(errorCode.getMessage(e));
        this.errorCode = errorCode;
    }
}
