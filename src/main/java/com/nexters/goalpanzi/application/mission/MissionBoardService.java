package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.MemberRanks;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionMembers;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class MissionBoardService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MissionBoardsResponse getBoard(final MissionBoardQuery query) {
        Member member = memberRepository.getMember(query.memberId());
        Mission mission = missionRepository.getMission(query.missionId());
        MissionMembers missionMembers = getMissionMembers(query.missionId(), query.sortType(), query.direction());
//        TODO 추후 활성화
//        missionMembers.verifyMissionMember(member);

        Map<Integer, List<Member>> boardMap = groupByVerificationCount(mission, missionMembers);
        List<MissionBoardResponse> boards = new ArrayList<>();
        for (Map.Entry<Integer, List<Member>> entry : boardMap.entrySet()) {
            boards.add(MissionBoardResponse.of(query.memberId(), entry.getKey(), entry.getValue()));
        }

        return new MissionBoardsResponse(
                missionMembers.getProgressCount(),
                MemberRanks.from(missionMembers.getMissionMembers()).getRankByMember(member).rank(),
                boards);
    }

    private MissionMembers getMissionMembers(final Long missionId, final MissionBoardQuery.SortType sortType, final Sort.Direction direction) {
        if (sortType.equals(MissionBoardQuery.SortType.RANDOM)) {
            return shuffleMissionMembers(missionId);
        }
        return sortMissionMembers(missionId, sortType, direction);
    }

    private MissionMembers shuffleMissionMembers(final Long missionId) {
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(missionId);
        Collections.shuffle(missionMembers);
        return new MissionMembers(missionMembers);
    }

    private MissionMembers sortMissionMembers(final Long missionId, final MissionBoardQuery.SortType sortType, final Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortType.getProperty());
        return new MissionMembers(
                missionMemberRepository.findAllByMissionId(missionId, sort)
        );
    }

    private Map<Integer, List<Member>> groupByVerificationCount(final Mission mission, final MissionMembers missionMembers) {
        Map<Integer, List<Member>> board = initializeBoard(mission.getBoardCount());

        for (Map.Entry<Integer, List<Member>> entry : board.entrySet()) {
            Integer verificationCount = entry.getKey();
            List<Member> members = missionMembers.getMissionMembersByBoardNumber(verificationCount).stream()
                    .map(MissionMember::getMember)
                    .toList();
            entry.setValue(members);
        }
        return board;
    }

    private Map<Integer, List<Member>> initializeBoard(final Integer boardCount) {
        return IntStream.range(0, boardCount + 1)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> new ArrayList<>()));
    }
}