package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.AuthService;
import com.nexters.goalpanzi.application.auth.dto.AppleLoginRequest;
import com.nexters.goalpanzi.application.auth.dto.JwtTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/apple")
    public ResponseEntity<JwtTokenResponse> loginApple(
            @RequestBody @Valid AppleLoginRequest appleLoginRequest
    ) {
        JwtTokenResponse response = authService.appleOAuthLogin(appleLoginRequest);

        return ResponseEntity.ok(response);
    }
}
