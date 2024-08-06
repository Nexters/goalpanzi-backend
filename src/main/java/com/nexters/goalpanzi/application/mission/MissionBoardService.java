package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class MissionBoardService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;

    @Transactional
    public List<MissionBoardResponse> getBoard(final MissionBoardQuery query) {
        return missionRepository.findById(query.missionId())
                .map(this::groupByVerificationCount)
                .map(this::sortByVerifiedAt)
                .map(this::convertToBoardResponse)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION, query.missionId()));
    }

    private Map<Integer, List<Member>> groupByVerificationCount(final Mission mission) {
        Map<Integer, List<Member>> board = initializeBoard(mission.getBoardCount());

        missionMemberRepository.findAllByMissionId(mission.getId())
                .forEach(m -> board.get(m.getVerificationCount()).add(m.getMember()));
        return board;
    }

    private Map<Integer, List<Member>> sortByVerifiedAt(final Map<Integer, List<Member>> groupedMembers) {
        return groupedMembers.entrySet().stream()
                .peek(entry -> entry.getValue().sort(Comparator.comparing(BaseEntity::getCreatedAt)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<MissionBoardResponse> convertToBoardResponse(final Map<Integer, List<Member>> groupedAndSortedMembers) {
        return groupedAndSortedMembers.entrySet().stream()
                .map(entry -> MissionBoardResponse.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<Integer, List<Member>> initializeBoard(final Integer boardCount) {
        return IntStream.range(0, boardCount + 1)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> new ArrayList<>()));
    }
}