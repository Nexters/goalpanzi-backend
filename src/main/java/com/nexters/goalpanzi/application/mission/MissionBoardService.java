package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class MissionBoardService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;

    @Transactional(readOnly = true)
    public MissionBoardsResponse getBoard(final MissionBoardQuery query) {
        Mission mission = missionRepository.getMission(query.missionId());
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());

        Map<Integer, List<Member>> missionMemberGroups = groupByVerificationCount(mission, missionMembers);

        List<MissionBoardResponse> boards = new ArrayList<>();
        for (Map.Entry<Integer, List<Member>> entry : missionMemberGroups.entrySet()) {
            boards.add(MissionBoardResponse.of(query.memberId(), query.orderBy(), entry.getKey(), entry.getValue()));
        }

        return new MissionBoardsResponse(countProgress(missionMembers), boards);
    }

    private Map<Integer, List<Member>> groupByVerificationCount(final Mission mission, final List<MissionMember> missionMembers) {
        Map<Integer, List<Member>> board = initializeBoard(mission.getBoardCount());

        missionMembers.forEach(missionMember -> board.get(missionMember.getVerificationCount()).add(missionMember.getMember()));

        return board;
    }

    private Map<Integer, List<Member>> initializeBoard(final Integer boardCount) {
        return IntStream.range(0, boardCount + 1)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> new ArrayList<>()));
    }

    private static int countProgress(final List<MissionMember> missionMembers) {
        return missionMembers.stream()
                .filter(missionMember ->
                        missionMember.getUpdatedAt().toLocalDate().equals(LocalDate.now()) && !missionMember.getVerificationCount().equals(0))
                .toList().size();
    }
}