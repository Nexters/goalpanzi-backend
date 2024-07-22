package com.nexters.goalpanzi.application.auth;

import com.nexters.goalpanzi.application.auth.dto.AppleLoginRequest;
import com.nexters.goalpanzi.application.auth.dto.GoogleLoginRequest;
import com.nexters.goalpanzi.application.auth.dto.LoginResponse;
import com.nexters.goalpanzi.application.auth.dto.TokenResponse;
import com.nexters.goalpanzi.config.jwt.Jwt;
import com.nexters.goalpanzi.config.jwt.JwtManager;
import com.nexters.goalpanzi.domain.auth.RefreshTokenRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.MemberRepository;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SocialUserProviderFactory socialUserProviderFactory;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtManager jwtManager;

    public LoginResponse appleOAuthLogin(final AppleLoginRequest request) {
        SocialUserProvider appleUserProvider = socialUserProviderFactory.getProvider(SocialType.APPLE);
        SocialUserInfo socialUserInfo = appleUserProvider.getSocialUserInfo(request.identityToken());

        return login(socialUserInfo.socialId(), socialUserInfo.email());
    }

    public LoginResponse googleOAuthLogin(final GoogleLoginRequest request) {
        SocialUserInfo socialUserInfo = new SocialUserInfo(request.identityToken(), request.email());

        return login(socialUserInfo.socialId(), socialUserInfo.email());
    }

    private LoginResponse login(final String socialId, final String email) {
        Member member = memberRepository.save(Member.socialLogin(socialId, email));

        String altKey = member.getAltKey();
        Jwt jwt = jwtManager.generateTokens(altKey);
        refreshTokenRepository.save(altKey, jwt.refreshToken(), jwt.refreshExpiresIn());

        return new LoginResponse(jwt.accessToken(), jwt.refreshToken(), member.isProfileSet());
    }

    public void logout(final String altKey) {
        refreshTokenRepository.delete(altKey);
    }

    public TokenResponse reissueToken(final String altKey, final String refreshToken) {
        validateRefreshToken(altKey, refreshToken);

        Jwt jwt = jwtManager.generateTokens(altKey);
        refreshTokenRepository.save(altKey, jwt.refreshToken(), jwt.refreshExpiresIn());

        return new TokenResponse(jwt.accessToken(), jwt.refreshToken());
    }

    private void validateRefreshToken(final String altKey, final String refreshToken) {
        String storedRefreshToken = refreshTokenRepository.find(altKey);
        
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtManager.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }
}