package com.nexters.goalpanzi.exception;

public record ErrorResponse(
        Integer code,
        String message
) {
}