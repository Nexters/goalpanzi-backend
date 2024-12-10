package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class MissionMemberRepositoryTest {

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    private final Member MEMBER = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE);

    @Test
    void 미션_멤버를_멤버와_미션과_함께_조회한다() {
        Member member = memberRepository.save(MEMBER);
        Mission mission = missionRepository.save(
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
        missionMemberRepository.save(MissionMember.join(member, mission));

        MissionMember missionMember = missionMemberRepository.getMissionMemberWithMemberAndMission(member.getId(), mission.getId());
        assertAll(
                () -> assertThat(missionMember.getMember()).isEqualTo(member),
                () -> assertThat(missionMember.getMission()).isEqualTo(mission)
        );
    }
}