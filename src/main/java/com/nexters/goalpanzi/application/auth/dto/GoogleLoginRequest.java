package com.nexters.goalpanzi.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record GoogleLoginRequest(
        @NotEmpty String identityToken,
        @NotEmpty String email
) {
}