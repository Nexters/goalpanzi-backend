package com.nexters.goalpanzi.application.auth.google;

import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GoogleIdentityToken {

    private static String SHA_256 = "SHA-256";

    public static String generate(final String email) {
        return generateHash(email + "__" + SocialType.GOOGLE);
    }

    private static String generateHash(final String input) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    MessageDigest.getInstance(SHA_256).digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new BaseException(ErrorCode.FAILED_TO_GENERATE_HASH);
        }
    }
}
