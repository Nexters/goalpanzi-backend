package com.nexters.goalpanzi.application.auth;

import com.nexters.goalpanzi.domain.member.SocialType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Component
class SocialUserProviderFactory {

    public Map<SocialType, SocialUserProvider> providers;

    public SocialUserProviderFactory(final Set<SocialUserProvider> providers) {
        this.providers = providers.stream()
                .collect(toMap(SocialUserProvider::getSocialType, strategy -> strategy));
    }

    public SocialUserProvider getProvider(final SocialType socialType) {
        return providers.get(socialType);
    }
}