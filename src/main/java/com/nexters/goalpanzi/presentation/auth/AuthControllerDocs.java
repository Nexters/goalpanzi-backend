package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.dto.AppleLoginRequest;
import com.nexters.goalpanzi.application.auth.dto.JwtTokenResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "인증/인가",
        description = """
                인증, 인가와 관련된 그룹입니다.

                APPLE, GOOGLE 로그인을 제공합니다.

                """
)
public interface AuthControllerDocs {

    @PostMapping("/login/apple")
    ResponseEntity<JwtTokenResponse> loginApple(
            @RequestBody @Valid AppleLoginRequest appleLoginRequest
    );
}
