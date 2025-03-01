package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_MEMBER_A;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class MissionVerificationValidatorTest extends IntegrationTest {

    @Autowired
    private MissionVerificationValidator sut;

    @Autowired
    private MissionVerificationRepository missionVerificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @AfterEach
    void tearDown() {
        missionVerificationRepository.deleteAllInBatch();
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 이미_완주한_미션은_검증에_실패한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, BOARD_COUNT));

        thenThrownBy(() -> sut.validate(missionMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ALREADY_COMPLETED_MISSION.getMessage());
    }

    @Test
    void 중복_인증을_시도하면_검증에_실패한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));
        missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

        thenThrownBy(() -> sut.validate(missionMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DUPLICATE_VERIFICATION.getMessage());
    }

    @Test
    void 미션_기간이_아니므로_검증에_실패한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now().minusDays(31),
                LocalDateTime.now().minusDays(1),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));

        thenThrownBy(() -> sut.validate(missionMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_VERIFICATION_PERIOD.getMessage());
    }

    @Test
    void 지정한_미션_요일이_아니므로_검증에_실패한다() {
//        thenThrownBy(() -> sut.validate(missionMember))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.NOT_VERIFICATION_DAY.getMessage());
    }

    @Test
    void 지정한_미션_시간대가_아니므로_검증에_실패한다() {
//        thenThrownBy(() -> sut.validate(missionMember))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.NOT_VERIFICATION_TIME.getMessage());
    }
}
