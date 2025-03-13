package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.then;

class MissionMemberRepositoryTest extends IntegrationTest {

    @Autowired
    private MissionMemberRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    @AfterEach
    void tearDown() {
        sut.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 미션_멤버를_멤버와_미션과_함께_조회한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(
                Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(31),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                )
        );
        sut.save(MissionMember.join(member, mission, LocalDateTime.now()));

        final MissionMember actual = sut.getMissionMemberWithMemberAndMission(member.getId(), mission.getId());

        then(actual)
                .extracting("member", "mission")
                .containsExactly(member, mission);
    }
}