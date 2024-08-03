package com.nexters.goalpanzi.domain.mission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationCodeTest {

    @Test
    void 참여코드는_네자리다() {
        InvitationCode code = new InvitationCode("AVD3");
        assertThat(code.getCode()).hasSize(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123738432849234"})
    void 참여코드가_올바르지_않은경우_에러가_발생한다(String code) {
        assertThatThrownBy(() -> new InvitationCode(code))
                .isInstanceOf(IllegalArgumentException.class);
    }
}