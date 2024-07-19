package com.nexters.goalpanzi.application.auth;

import com.nexters.goalpanzi.domain.member.SocialType;

public interface SocialUserProvider {

    SocialType getSocialType();

    SocialUserInfo getSocialUserInfo(final String identityToken);
}
