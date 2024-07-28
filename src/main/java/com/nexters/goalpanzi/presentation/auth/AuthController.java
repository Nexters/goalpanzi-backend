package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.AuthService;
import com.nexters.goalpanzi.application.auth.dto.*;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/login/apple")
    public ResponseEntity<LoginResponse> loginApple(
            @RequestBody @Valid final AppleLoginCommand appleLoginCommand
    ) {
        LoginResponse response = authService.appleOAuthLogin(appleLoginCommand);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginGoogle(
            @RequestBody @Valid final GoogleLoginCommand googleLoginCommand
    ) {
        LoginResponse response = authService.googleOAuthLogin(googleLoginCommand);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @LoginMemberId final String userKey
    ) {
        authService.logout(userKey);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PostMapping("/token:reissue")
    public ResponseEntity<TokenResponse> reissueToken(
            @RequestBody @Valid final RefreshTokenCommand refreshTokenCommand,
            @LoginMemberId final String userId
    ) {
        TokenResponse tokenResponse = authService.reissueToken(userId, refreshTokenCommand.refreshToken());

        return ResponseEntity.ok(tokenResponse);
    }
}
