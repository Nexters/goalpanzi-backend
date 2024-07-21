package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.AuthService;
import com.nexters.goalpanzi.application.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestBody @Valid final AppleLoginRequest appleLoginRequest
    ) {
        LoginResponse response = authService.appleOAuthLogin(appleLoginRequest);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginGoogle(
            @RequestBody @Valid final GoogleLoginRequest googleLoginRequest
    ) {
        LoginResponse response = authService.googleOAuthLogin(googleLoginRequest);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> logout(final HttpServletRequest request) {
        authService.logout(request.getHeader("altKey"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PostMapping("/token:reissue")
    public ResponseEntity<TokenResponse> reissueToken(
            @RequestBody @Valid final TokenRequest tokenRequest,
            final HttpServletRequest request
    ) {
        TokenResponse tokenResponse = authService.reissueToken(request.getHeader("altKey"), tokenRequest.refreshToken());

        return ResponseEntity.ok(tokenResponse);
    }
}
