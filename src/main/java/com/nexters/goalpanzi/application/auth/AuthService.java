package com.nexters.goalpanzi.application.auth;

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

    public void appleOAuthLogin(String identityToken) {
        SocialUserProvider appleUserProvider = socialUserProviderFactory.getProvider(SocialType.APPLE);
        SocialUserInfo socialUserInfo = appleUserProvider.getSocialUserInfo(identityToken);

        memberRepository.save(Member.init(socialUserInfo.email()));
    }

    public void googleOAuthLogin() {
        // TODO()
    }
}
