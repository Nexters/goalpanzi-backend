package com.nexters.goalpanzi.application.history;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.fixture.MemberFixture;
import com.nexters.goalpanzi.fixture.MissionFixture;
import com.nexters.goalpanzi.fixture.MissionVerificationFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class HistoryServiceTest extends IntegrationTest {

    @Autowired
    private HistoryService historyService;

    @MockBean
    private MissionRepository missionRepository;

    @MockBean
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private MissionVerificationRepository missionVerificationRepository;

    @Test
    void 완료된_미션_기록을_조회한다() {
        // given
        Member member = Member.socialLogin("abc", "email", SocialType.GOOGLE);
        Mission mission = MissionFixture.create();

        int VERIFICATION_COUNT = 1;
        MissionMember missionMember = new MissionMember(member, mission, VERIFICATION_COUNT);
        MissionVerification missionVerification = MissionVerificationFixture.create(mission, member, MissionFixture.UPLOADED_IMAGE_URL, mission.getBoardCount());
        ReflectionTestUtils.setField(mission, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 1L);
        when(missionRepository.findAllByIdIn(any())).thenReturn(List.of(mission));
        when(missionMemberRepository.findByMemberIdAndMissionStatus(any(),any(),any())).thenReturn(
                new SliceImpl<>(List.of(missionMember),PageRequest.ofSize(1), false));
        when(missionMemberRepository.findAllByMissionIdIn(any())).thenReturn(List.of(missionMember));
        when(missionVerificationRepository.findByMemberIdAndMissionIdIn(any(), any())).thenReturn(List.of(missionVerification));

        // when
        var actual = historyService.getMissionHistories(
                MemberFixture.MEMBER_ID,
                PageRequest.ofSize(10)
        );

        // then
        assertAll(
                () -> assertThat(actual.hasNext()).isEqualTo(false),
                () -> assertThat(actual.resultList()).hasSize(1),
                () -> assertThat(actual.resultList().getFirst().myVerificationCount()).isEqualTo(VERIFICATION_COUNT),
                () -> assertThat(actual.resultList().getFirst().totalVerificationCount()).isEqualTo(mission.getBoardCount()),
                () -> assertThat(actual.resultList().getFirst().rank()).isEqualTo(1));
    }
}