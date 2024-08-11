package com.nexters.goalpanzi.presentation.auth;

import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.request.RefreshTokenCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.auth.dto.response.TokenResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Apple 로그인", description = "Apple 로그인을 처리합니다.")
    @PostMapping("/login/apple")
    ResponseEntity<LoginResponse> loginApple(
            @RequestBody @Valid final AppleLoginCommand appleLoginCommand
    );

    @Operation(summary = "Google 로그인", description = "Google 로그인을 처리합니다.")
    @PostMapping("/login/google")
    ResponseEntity<LoginResponse> loginGoogle(
            @RequestBody @Valid final GoogleLoginCommand googleLoginCommand
    );

    @Operation(summary = "로그아웃", description = "로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Parameter(hidden = true) @LoginMemberId final Long memberId);

    @Operation(summary = "토큰 재발급", description = "access 토큰과 refresh 토큰을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - 만료된 refresh 토큰이거나 갱신되어 더 이상 유효하지 않은 refresh 토큰", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/token:reissue")
    ResponseEntity<TokenResponse> reissueToken(
            @RequestBody @Valid final RefreshTokenCommand refreshTokenCommand,
            @Parameter(hidden = true) @LoginMemberId final Long memberId
    );
}
