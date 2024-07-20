package com.nexters.goalpanzi.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record AppleLoginRequest(
        @NotEmpty String identityToken
) {
}
