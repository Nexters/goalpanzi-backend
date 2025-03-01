package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.fixture.MissionFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class MissionBoardServiceTest extends IntegrationTest {

    @Autowired
    MissionBoardService sut;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    MissionMemberRepository missionMemberRepository;

    @AfterEach
    void tearDown() {
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 보드판_정보를_조회한다() {
        final Member member1 = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
        final Member member2 = memberRepository.save(Member.socialLogin("socialId2", EMAIL_HOST, SocialType.GOOGLE));
        final Member member3 = memberRepository.save(Member.socialLogin("socialId3", EMAIL_HOST, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(MissionFixture.create());
        final MissionMember missionMember1 = missionMemberRepository.save(new MissionMember(member1, mission, 1));
        final MissionMember missionMember2 = missionMemberRepository.save(new MissionMember(member2, mission, 1));
        final MissionMember missionMember3 = missionMemberRepository.save(new MissionMember(member3, mission, 2));

        final MissionBoardQuery query =
                new MissionBoardQuery(member1.getId(), 1L, MissionBoardQuery.SortType.RANK, Sort.Direction.ASC);
        final MissionBoardsResponse actual = sut.getBoard(query);

        assertAll(
                () -> then(actual.rank()).isEqualTo(2),
                () -> then(actual.missionBoards())
                        .hasSize(mission.getBoardCount() + 1)
                        .extracting(
                                MissionBoardResponse::number,
                                MissionBoardResponse::isMyPosition,
                                it -> it.missionBoardMembers().size()
                        )
                        .containsExactly(
                                tuple(0, false, 0),
                                tuple(1, true, 2),
                                tuple(2, false, 1),
                                tuple(3, false, 0),
                                tuple(4, false, 0),
                                tuple(5, false, 0),
                                tuple(6, false, 0),
                                tuple(7, false, 0),
                                tuple(8, false, 0),
                                tuple(9, false, 0),
                                tuple(10, false, 0)
                        )
        );
    }
}