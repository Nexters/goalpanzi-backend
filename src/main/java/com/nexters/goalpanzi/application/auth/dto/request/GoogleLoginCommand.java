package com.nexters.goalpanzi.application.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record GoogleLoginCommand(
        @NotEmpty String identityToken,
        @NotEmpty String email
) {
}