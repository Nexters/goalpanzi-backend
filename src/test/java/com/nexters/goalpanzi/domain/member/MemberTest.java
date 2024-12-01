package com.nexters.goalpanzi.domain.member;

import org.junit.jupiter.api.Test;

import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void 닉네임_설정이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateNickname(NICKNAME_HOST);

        assertThat(member.getNickname()).isEqualTo(NICKNAME_HOST);
    }

    @Test
    void 장기말타입_설정이_가능하다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateCharacterType(CHARACTER_HOST);

        assertThat(member.getCharacterType()).isEqualTo(CHARACTER_HOST);
    }

    @Test
    void 닉네임과_장기말타입_전부_설정시_프로필_설정여부를_true로_반환한다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateNickname(NICKNAME_HOST);
        member.updateCharacterType(CHARACTER_HOST);

        assertThat(member.isProfileSet()).isTrue();
    }

    @Test
    void 닉네임과_장기말타입_중_하나라도_설정되지_않은경우_프로필_설정여부를_false로_반환한다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateNickname(NICKNAME_HOST);

        assertThat(member.isProfileSet()).isFalse();
    }

    @Test
    void 디바이스_토큰을_갱신한다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateDeviceToken(DEVICE_TOKEN);

        assertThat(member.getDeviceToken()).isEqualTo(DEVICE_TOKEN);
    }

    @Test
    void 푸시_알림_활성화_여부를_수정한다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updateDeviceToken(DEVICE_TOKEN);
        member.updatePushActivationStatus(true);

        assertThat(member.isPushActivated()).isTrue();
    }

    @Test
    void 푸시_알림을_활성화해도_디바이스_토큰이_null인_경우_비활성화_상태로_판단한다() {
        Member member = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        member.updatePushActivationStatus(true);

        assertThat(member.isPushActivated()).isFalse();
    }
}