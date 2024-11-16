package com.nexters.goalpanzi.exception;

public record ErrorResponse(
        Integer status,
        String message,
        ErrorCode errorCode
) {
}