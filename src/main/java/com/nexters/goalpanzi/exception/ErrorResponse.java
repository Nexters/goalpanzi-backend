package com.nexters.goalpanzi.exception;

public record ErrorResponse(
        int code,
        String message
) {
}