package com.nexters.goalpanzi.application.auth.apple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class AppleApiCallerTest {

    @Autowired
    private AppleApiCaller appleApiCaller;

    @Test
    void 애플_서버에_PUBLIC_KEY를_요청한다() {
        ApplePublicKeys applePublicKeys = appleApiCaller.getApplePublicKeys();
        List<ApplePublicKey> keys = applePublicKeys.getKeys();

        assertAll(
                () -> assertThat(keys).isNotEmpty(),
                () -> assertThat(keys).doesNotContainNull()
        );
    }
}