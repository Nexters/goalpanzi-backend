package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.exception.UnauthorizedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchesKey(final String alg, final String kid) {
        return this.keys
                .stream()
                .filter(k -> k.alg().equals(alg) && k.kid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."));
    }
}
