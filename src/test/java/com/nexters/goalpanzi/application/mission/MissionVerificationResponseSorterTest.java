package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerificationView;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.fixture.MissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class MissionVerificationResponseSorterTest {

    Member me;
    Mission mission;
    List<MissionMember> missionMembers;

    @MockBean
    MissionVerificationViewRepository missionVerificationViewRepository;

    @Autowired
    MissionVerificationResponseSorter missionVerificationResponseSorter;

    @BeforeEach
    void setUp() {
        me = mock(Member.class);
        when(me.getNickname()).thenReturn(NICKNAME_HOST);
        when(me.getId()).thenReturn(1L);

        Member memberA = mock(Member.class);
        when(memberA.getNickname()).thenReturn(NICKNAME_MEMBER_A);
        when(memberA.getId()).thenReturn(2L);

        Member memberB = mock(Member.class);
        when(memberB.getNickname()).thenReturn(NICKNAME_MEMBER_B);
        when(memberB.getId()).thenReturn(3L);

        mission = MissionFixture.create();
        missionMembers = List.of(
                new MissionMember(me, mission, 0),
                new MissionMember(memberA, mission, 0),
                new MissionMember(memberB, mission, 0)
        );
    }

    @Test
    void 미션_인증_현황의_개수는_항상_미션에_참여한_멤버의_수와_일치한다() {
        List<MissionVerification> missionVerifications = List.of();

        when(missionVerificationViewRepository.getMissionVerificationView(any(), any())).thenReturn(null);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.ASC, missionVerifications, missionMembers);
        assertThat(responses.size()).isEqualTo(missionMembers.size());
    }

    @Test
    void 나의_미션_인증_현황은_항상_0번째에_온다() {
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(missionVerificationOfMe);

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(missionVerificationViewRepository.getMissionVerificationView(any(), any())).thenReturn(null);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.DESC, missionVerifications, missionMembers);
        assertThat(responses.getFirst().nickname()).isEqualTo(me.getNickname());
    }

    @Test
    void 미션_인증을_하지_않은_멤버는_마지막에_온다() {
        Member unverifiedMember = mock(Member.class);
        MissionMember unverifiedMissionMember = new MissionMember(unverifiedMember, mission, 0);
        List<MissionMember> newMissionMembers = new ArrayList<>(missionMembers);
        newMissionMembers.add(unverifiedMissionMember);
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification viewedMissionVerification = mock(MissionVerification.class);
        MissionVerification unviewedMissionVerification = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                viewedMissionVerification,
                unviewedMissionVerification
        );
        MissionVerificationView missionVerificationView = mock(MissionVerificationView.class);

        when(unverifiedMember.getNickname()).thenReturn("lazy member");
        when(unverifiedMember.getId()).thenReturn(4L);

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(viewedMissionVerification.getId()).thenReturn(2L);
        when(viewedMissionVerification.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(viewedMissionVerification.getMission()).thenReturn(mission);
        when(viewedMissionVerification.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(unviewedMissionVerification.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(unviewedMissionVerification.getMission()).thenReturn(mission);
        when(unviewedMissionVerification.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(missionVerificationView.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(missionVerificationViewRepository.getMissionVerificationView(viewedMissionVerification.getId(), me.getId())).thenReturn(missionVerificationView);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.DESC, missionVerifications, newMissionMembers);
        assertThat(responses.getLast().nickname()).isEqualTo(unverifiedMember.getNickname());
    }

    @Test
    void 미션_인증_현황을_인증_시간_최신순으로_정렬한다() {
        LocalDateTime now = LocalDateTime.now();
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification missionVerificationOfEarliest = mock(MissionVerification.class);
        MissionVerification missionVerificationOfLatest = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                missionVerificationOfEarliest,
                missionVerificationOfLatest
        );

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(now);

        when(missionVerificationOfEarliest.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(missionVerificationOfEarliest.getCreatedAt()).thenReturn(now.minusHours(1));

        when(missionVerificationOfLatest.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(missionVerificationOfLatest.getCreatedAt()).thenReturn(now.plusHours(1));

        when(missionVerificationViewRepository.getMissionVerificationView(any(), any())).thenReturn(null);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.DESC, missionVerifications, missionMembers);
        assertAll(
                () -> assertThat(responses.get(0).nickname()).isEqualTo(me.getNickname()),
                () -> assertThat(responses.get(1).nickname()).isEqualTo(missionVerificationOfLatest.getMember().getNickname()),
                () -> assertThat(responses.get(2).nickname()).isEqualTo(missionVerificationOfEarliest.getMember().getNickname())
        );
    }

    @Test
    void 미션_인증_현황을_인증_시간_오래된순으로_정렬한다() {
        LocalDateTime now = LocalDateTime.now();
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification missionVerificationOfEarliest = mock(MissionVerification.class);
        MissionVerification missionVerificationOfLatest = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                missionVerificationOfEarliest,
                missionVerificationOfLatest
        );

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(now);

        when(missionVerificationOfEarliest.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(missionVerificationOfEarliest.getCreatedAt()).thenReturn(now.minusHours(1));

        when(missionVerificationOfLatest.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(missionVerificationOfLatest.getCreatedAt()).thenReturn(now.plusHours(1));

        when(missionVerificationViewRepository.getMissionVerificationView(any(), any())).thenReturn(null);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.ASC, missionVerifications, missionMembers);
        assertAll(
                () -> assertThat(responses.get(0).nickname()).isEqualTo(me.getNickname()),
                () -> assertThat(responses.get(1).nickname()).isEqualTo(missionVerificationOfEarliest.getMember().getNickname()),
                () -> assertThat(responses.get(2).nickname()).isEqualTo(missionVerificationOfLatest.getMember().getNickname())
        );
    }

    @Test
    void 내가_확인하지_않은_미션_인증_현황은_내가_확인한_미션_인증_현황의_앞에_온다() {
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification viewedMissionVerification = mock(MissionVerification.class);
        MissionVerification unviewedMissionVerification = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                viewedMissionVerification,
                unviewedMissionVerification
        );
        MissionVerificationView missionVerificationView = mock(MissionVerificationView.class);

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getMission()).thenReturn(mission);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(viewedMissionVerification.getId()).thenReturn(2L);
        when(viewedMissionVerification.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(viewedMissionVerification.getMission()).thenReturn(mission);
        when(viewedMissionVerification.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(unviewedMissionVerification.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(unviewedMissionVerification.getMission()).thenReturn(mission);
        when(unviewedMissionVerification.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(missionVerificationView.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(missionVerificationViewRepository.getMissionVerificationView(viewedMissionVerification.getId(), me.getId())).thenReturn(missionVerificationView);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.ASC, missionVerifications, missionMembers);
        assertAll(
                () -> assertThat(responses.get(0).nickname()).isEqualTo(me.getNickname()),
                () -> assertThat(responses.get(1).nickname()).isEqualTo(unviewedMissionVerification.getMember().getNickname()),
                () -> assertThat(responses.get(2).nickname()).isEqualTo(viewedMissionVerification.getMember().getNickname())
        );
    }

    @Test
    void 내가_확인한_미션_인증_현황을_인증_시간_최신순으로_정렬한다() {
        LocalDateTime now = LocalDateTime.now();
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification viewedMissionVerificationOfEarliest = mock(MissionVerification.class);
        MissionVerification viewedMissionVerificationOfLatest = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                viewedMissionVerificationOfEarliest,
                viewedMissionVerificationOfLatest
        );
        MissionVerificationView missionVerificationView = mock(MissionVerificationView.class);

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getMission()).thenReturn(mission);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(now);

        when(viewedMissionVerificationOfEarliest.getId()).thenReturn(2L);
        when(viewedMissionVerificationOfEarliest.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(viewedMissionVerificationOfEarliest.getMission()).thenReturn(mission);
        when(viewedMissionVerificationOfEarliest.getCreatedAt()).thenReturn(now.minusHours(1));

        when(viewedMissionVerificationOfLatest.getId()).thenReturn(3L);
        when(viewedMissionVerificationOfLatest.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(viewedMissionVerificationOfLatest.getMission()).thenReturn(mission);
        when(viewedMissionVerificationOfLatest.getCreatedAt()).thenReturn(now.plusHours(1));

        when(missionVerificationView.getCreatedAt()).thenReturn(now);

        when(missionVerificationViewRepository.getMissionVerificationView(2L, me.getId())).thenReturn(missionVerificationView);
        when(missionVerificationViewRepository.getMissionVerificationView(3L, me.getId())).thenReturn(missionVerificationView);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.DESC, missionVerifications, missionMembers);
        assertAll(
                () -> assertThat(responses.get(0).nickname()).isEqualTo(me.getNickname()),
                () -> assertThat(responses.get(1).nickname()).isEqualTo(viewedMissionVerificationOfLatest.getMember().getNickname()),
                () -> assertThat(responses.get(2).nickname()).isEqualTo(viewedMissionVerificationOfEarliest.getMember().getNickname())
        );
    }

    @Test
    void 내가_확인한_미션_인증_현황을_인증_시간_오래된순으로_정렬한다() {
        LocalDateTime now = LocalDateTime.now();
        MissionVerification missionVerificationOfMe = mock(MissionVerification.class);
        MissionVerification viewedMissionVerificationOfEarliest = mock(MissionVerification.class);
        MissionVerification viewedMissionVerificationOfLatest = mock(MissionVerification.class);
        List<MissionVerification> missionVerifications = List.of(
                missionVerificationOfMe,
                viewedMissionVerificationOfEarliest,
                viewedMissionVerificationOfLatest
        );
        MissionVerificationView missionVerificationView = mock(MissionVerificationView.class);

        when(missionVerificationOfMe.getMember()).thenReturn(me);
        when(missionVerificationOfMe.getMission()).thenReturn(mission);
        when(missionVerificationOfMe.getCreatedAt()).thenReturn(now);

        when(viewedMissionVerificationOfEarliest.getId()).thenReturn(2L);
        when(viewedMissionVerificationOfEarliest.getMember()).thenReturn(missionMembers.get(1).getMember());
        when(viewedMissionVerificationOfEarliest.getMission()).thenReturn(mission);
        when(viewedMissionVerificationOfEarliest.getCreatedAt()).thenReturn(now.minusHours(1));

        when(viewedMissionVerificationOfLatest.getId()).thenReturn(3L);
        when(viewedMissionVerificationOfLatest.getMember()).thenReturn(missionMembers.get(2).getMember());
        when(viewedMissionVerificationOfLatest.getMission()).thenReturn(mission);
        when(viewedMissionVerificationOfLatest.getCreatedAt()).thenReturn(now.plusHours(1));

        when(missionVerificationView.getCreatedAt()).thenReturn(now);

        when(missionVerificationViewRepository.getMissionVerificationView(2L, me.getId())).thenReturn(missionVerificationView);
        when(missionVerificationViewRepository.getMissionVerificationView(3L, me.getId())).thenReturn(missionVerificationView);

        List<MissionVerificationResponse> responses = missionVerificationResponseSorter.sort(me, MissionVerificationQuery.SortType.VERIFIED_AT, Sort.Direction.ASC, missionVerifications, missionMembers);
        assertAll(
                () -> assertThat(responses.get(0).nickname()).isEqualTo(me.getNickname()),
                () -> assertThat(responses.get(1).nickname()).isEqualTo(viewedMissionVerificationOfEarliest.getMember().getNickname()),
                () -> assertThat(responses.get(2).nickname()).isEqualTo(viewedMissionVerificationOfLatest.getMember().getNickname())
        );
    }
}