package com.nexters.goalpanzi.domain.member;

import lombok.Getter;

@Getter
public enum SocialType {
    GOOGLE("TODO"), APPLE("appleUserInfoProvider");

    private final String userInfoProvider;

    SocialType(String userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }
}
