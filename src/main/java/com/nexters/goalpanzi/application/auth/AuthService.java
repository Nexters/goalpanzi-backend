package com.nexters.goalpanzi.application.auth;

import com.nexters.goalpanzi.application.auth.dto.AppleLoginRequest;
import com.nexters.goalpanzi.application.auth.dto.JwtTokenResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.MemberRepository;
import com.nexters.goalpanzi.domain.member.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SocialUserProviderFactory socialUserProviderFactory;
    private final MemberRepository memberRepository;

    public JwtTokenResponse appleOAuthLogin(AppleLoginRequest request) {
        SocialUserProvider appleUserProvider = socialUserProviderFactory.getProvider(SocialType.APPLE);
        SocialUserInfo socialUserInfo = appleUserProvider.getSocialUserInfo(request.identityToken());

        Member member = memberRepository.save(Member.socialLogin(socialUserInfo.socialId(), socialUserInfo.email()));

        // TODO: accessToken, refreshToken 발급

        return new JwtTokenResponse("TODO", "TODO", member.isProfileSet());
    }

    public void googleOAuthLogin() {
        // TODO()
    }
}
