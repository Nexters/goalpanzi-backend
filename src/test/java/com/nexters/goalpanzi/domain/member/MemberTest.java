package com.nexters.goalpanzi.domain.member;

import org.junit.jupiter.api.Test;

import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {

    @Test
    void 프로필_생성이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateProfile(NICKNAME_HOST, CharacterType.CAT);

        assertAll(
                () -> assertThat(member.isProfileSet()).isTrue(),
                () -> assertThat(member.getNickname()).isEqualTo(NICKNAME_HOST),
                () -> assertThat(member.getCharacterType()).isEqualTo(CharacterType.CAT)
        );
    }

    @Test
    void 프로필_생성후_변경이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateProfile(NICKNAME_HOST, CharacterType.CAT);
        member.updateProfile(NICKNAME_HOST, null);

        assertAll(
                () -> assertThat(member.isProfileSet()).isTrue(),
                () -> assertThat(member.getNickname()).isEqualTo(NICKNAME_HOST),
                () -> assertThat(member.getCharacterType()).isEqualTo(CharacterType.CAT)
        );
    }

    @Test
    void 닉네임만_설정이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateProfile(NICKNAME_HOST, null);

        assertAll(
                () -> assertThat(member.isProfileSet()).isFalse(),
                () -> assertThat(member.getNickname()).isEqualTo(NICKNAME_HOST)
        );
    }

    @Test
    void 캐릭터만_설정이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateProfile(null, CharacterType.CAT);

        assertAll(
                () -> assertThat(member.isProfileSet()).isFalse(),
                () -> assertThat(member.getCharacterType()).isEqualTo(CharacterType.CAT)
        );
    }
}