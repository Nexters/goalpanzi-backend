package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery.SortType;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

class MissionVerificationResponseSorterTest extends IntegrationTest {

    @Autowired
    private MissionVerificationResponseSorter sut;

    @Autowired
    private MissionVerificationViewRepository missionVerificationViewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @Autowired
    private MissionVerificationRepository missionVerificationRepository;

    @AfterEach
    void tearDown() {
        missionVerificationViewRepository.deleteAllInBatch();
        missionVerificationRepository.deleteAllInBatch();
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 미션_인증_응답의_개수는_인증_내역과_무관하게_항상_미션에_참여한_멤버의_수와_일치한다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 0),
                new MissionMember(member2, mission, 0),
                new MissionMember(member3, mission, 0)
        ));

        final List<MissionVerificationResponse> actual = sut.sort(member1, SortType.VERIFIED_AT, Sort.Direction.ASC, List.of(), missionMembers);

        then(actual).hasSize(missionMembers.size());
    }

    @Test
    void 나의_미션_인증_응답은_나의_인증_내역과_무관하게_항상_0번째에_온다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 0),
                new MissionMember(member2, mission, 0),
                new MissionMember(member3, mission, 0)
        ));

        final List<MissionVerificationResponse> actual = sut.sort(member1, SortType.VERIFIED_AT, Sort.Direction.ASC, List.of(), missionMembers);

        then(actual.getFirst().nickname()).isEqualTo(member1.getNickname());
    }


    @Test
    void 미션_인증을_하지_않은_멤버는_마지막에_온다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 1),
                new MissionMember(member2, mission, 1),
                new MissionMember(member3, mission, 0)
        ));
        final List<MissionVerification> verifications = missionVerificationRepository.saveAll(List.of(
                new MissionVerification(member1, mission, UPLOADED_IMAGE_URL, 1),
                new MissionVerification(member2, mission, UPLOADED_IMAGE_URL, 1)
        ));

        final List<MissionVerificationResponse> actual = sut.sort(member1, SortType.VERIFIED_AT, Sort.Direction.ASC, verifications, missionMembers);

        then(actual.getLast().nickname()).isEqualTo(member3.getNickname());
    }

    @Test
    void 미션_인증_현황을_인증_시간_최신순으로_정렬한다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 1),
                new MissionMember(member2, mission, 1),
                new MissionMember(member3, mission, 1)
        ));
        final LocalDateTime now = LocalDateTime.now();
        final MissionVerification verification1 = createMissionVerification(member1, mission, UPLOADED_IMAGE_URL, 1, now);
        final MissionVerification verification2 = createMissionVerification(member2, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(2));
        final MissionVerification verification3 = createMissionVerification(member3, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(1));

        System.out.println(verification1.getCreatedAt());
        System.out.println(verification3.getCreatedAt());
        final List<MissionVerificationResponse> actual = sut.sort(
                member1,
                SortType.VERIFIED_AT,
                Sort.Direction.DESC,
                List.of(verification1, verification2, verification3),
                missionMembers
        );

        then(actual)
                .extracting("nickname")
                .containsExactly(member1.getNickname(), member3.getNickname(), member2.getNickname());
    }

    @Test
    void 미션_인증_현황을_인증_시간_오래된_순으로_정렬한다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 1),
                new MissionMember(member2, mission, 1),
                new MissionMember(member3, mission, 1)
        ));
        final LocalDateTime now = LocalDateTime.now();
        final MissionVerification verification1 = createMissionVerification(member1, mission, UPLOADED_IMAGE_URL, 1, now);
        final MissionVerification verification2 = createMissionVerification(member2, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(2));
        final MissionVerification verification3 = createMissionVerification(member3, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(1));

        final List<MissionVerificationResponse> actual = sut.sort(
                member1,
                SortType.VERIFIED_AT,
                Sort.Direction.ASC,
                List.of(verification1, verification2, verification3),
                missionMembers
        );

        then(actual)
                .extracting("nickname")
                .containsExactly(member1.getNickname(), member2.getNickname(), member3.getNickname());
    }

    @Test
    void 내가_확인하지_않은_미션_인증_내역은_내가_확인한_미션_인증_내역의_앞에_온다() {
        final Member member1 = createMember("socialId1", EMAIL_HOST, SocialType.GOOGLE, NICKNAME_HOST);
        final Member member2 = createMember("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE, NICKNAME_MEMBER_A);
        final Member member3 = createMember("socialId3", EMAIL_MEMBER_B, SocialType.GOOGLE, NICKNAME_MEMBER_B);
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        final List<MissionMember> missionMembers = missionMemberRepository.saveAll(List.of(
                new MissionMember(member1, mission, 1),
                new MissionMember(member2, mission, 1),
                new MissionMember(member3, mission, 1)
        ));
        final LocalDateTime now = LocalDateTime.now();
        final MissionVerification verification1 = createMissionVerification(member1, mission, UPLOADED_IMAGE_URL, 1, now);
        final MissionVerification verification2 = createMissionVerification(member2, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(2));
        final MissionVerification verification3 = createMissionVerification(member3, mission, UPLOADED_IMAGE_URL, 1, now.minusSeconds(1));
        final MissionVerificationView view = missionVerificationViewRepository.save(new MissionVerificationView(verification2, member1));

        final List<MissionVerificationResponse> actual = sut.sort(
                member1,
                SortType.VERIFIED_AT,
                Sort.Direction.DESC,
                List.of(verification1, verification2, verification3),
                missionMembers
        );

        then(actual)
                .extracting("nickname", "viewedAt")
                .containsExactly(
                        tuple(member1.getNickname(), null),
                        tuple(member3.getNickname(), null),
                        tuple(member2.getNickname(), view.getCreatedAt()));
    }

    private Member createMember(final String socialId, final String email, final SocialType socialType, final String nickname) {
        final Member member = memberRepository.save(Member.socialLogin(socialId, email, socialType));
        member.updateNickname(nickname);
        return member;
    }

    private MissionVerification createMissionVerification(final Member member, final Mission mission, final String imageUrl, final Integer boardNumber, final LocalDateTime createdAt) {
        final MissionVerification verification = missionVerificationRepository.save(new MissionVerification(member, mission, imageUrl, boardNumber));
        ReflectionTestUtils.setField(verification, "createdAt", createdAt);
        return verification;
    }
}