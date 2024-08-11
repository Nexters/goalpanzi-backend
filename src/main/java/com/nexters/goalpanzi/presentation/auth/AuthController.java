package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.AuthService;
import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.request.RefreshTokenCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.auth.dto.response.TokenResponse;
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
            @LoginMemberId final Long memberId
    ) {
        authService.logout(memberId);

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/token:reissue")
    public ResponseEntity<TokenResponse> reissueToken(
            @RequestBody @Valid final RefreshTokenCommand refreshTokenCommand,
            @LoginMemberId final Long memberId
    ) {
        TokenResponse tokenResponse = authService.reissueToken(memberId, refreshTokenCommand.refreshToken());

        return ResponseEntity.ok(tokenResponse);
    }
}
