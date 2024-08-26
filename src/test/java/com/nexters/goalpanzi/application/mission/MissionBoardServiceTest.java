package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.fixture.MissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class MissionBoardServiceTest {

    Member me;
    List<Member> members;

    @MockBean
    MissionRepository missionRepository;

    @MockBean
    MissionMemberRepository missionMemberRepository;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    MissionBoardService missionBoardService;

    @BeforeEach
    void setUp() {
        me = mock(Member.class);
        when(me.getId()).thenReturn(1L);

        Member member1 = mock(Member.class);
        when(member1.getId()).thenReturn(2L);

        Member member2 = mock(Member.class);
        when(member2.getId()).thenReturn(3L);

        members = List.of(me, member1, member2);
    }

    @Test
    void 보드판_정보를_조회한다() {
        Mission mission = MissionFixture.create();
        MissionMember missionMember1 = mock(MissionMember.class);
        MissionMember missionMember2 = mock(MissionMember.class);
        MissionMember missionMember3 = mock(MissionMember.class);
        MissionBoardQuery query = new MissionBoardQuery(me.getId(), 1L, MissionBoardQuery.SortType.RANK, Sort.Direction.ASC);

        LocalDateTime now = LocalDateTime.now();
        when(missionMember1.getUpdatedAt()).thenReturn(now);
        when(missionMember1.getVerificationCount()).thenReturn(1);
        when(missionMember1.getMember()).thenReturn(members.get(0));

        when(missionMember2.getUpdatedAt()).thenReturn(now);
        when(missionMember2.getVerificationCount()).thenReturn(1);
        when(missionMember2.getMember()).thenReturn(members.get(1));

        when(missionMember3.getUpdatedAt()).thenReturn(now);
        when(missionMember3.getVerificationCount()).thenReturn(2);
        when(missionMember3.getMember()).thenReturn(members.get(2));

        when(memberRepository.getMember(me.getId())).thenReturn(me);
        when(missionRepository.getMission(anyLong())).thenReturn(mission);
        when(missionMemberRepository.findAllByMissionId(anyLong(), any())).thenReturn(List.of(missionMember1, missionMember2, missionMember3));

        MissionBoardsResponse response = missionBoardService.getBoard(query);
        assertAll(
                () -> assertThat(response.missionBoards().size()).isEqualTo(mission.getBoardCount() + 1),
                () -> assertThat(response.rank()).isEqualTo(2),
                () -> assertThat(response.progressCount()).isEqualTo(3),

                () -> assertThat(response.missionBoards().get(1).isMyPosition()).isTrue(),
                () -> assertThat(response.missionBoards().get(1).missionBoardMembers().size()).isEqualTo(2),
                () -> assertThat(response.missionBoards().get(2).missionBoardMembers().size()).isEqualTo(1)
        );
    }
}