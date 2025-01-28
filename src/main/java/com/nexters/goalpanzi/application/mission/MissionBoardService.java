package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionBoardService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationRepository missionVerificationRepository;
    private final MemberRepository memberRepository;

    public MissionBoardsResponse getBoard(final MissionBoardQuery query) {
        Member member = memberRepository.getMember(query.memberId());
        Mission mission = missionRepository.getMission(query.missionId());
        MissionMembers missionMembers = getMissionMembers(query.missionId(), query.sortType(), query.direction());
        missionMembers.verifyMissionMember(member);

        // key: 보드칸 번호 value: 참여 멤버 목록
        Map<Integer, List<Member>> boardMap = groupByVerificationCount(mission, missionMembers);

        // key: 보드칸 번호 value: 미션 인증 정보
        Map<Integer, MissionVerification> boardCountMap = missionVerificationRepository.findByMemberIdAndMissionId(
                        member.getId(), mission.getId(), Pageable.unpaged())
                .stream()
                .collect(Collectors.toMap(MissionVerification::getBoardNumber, verification -> verification));

        var boards = boardMap.entrySet().stream()
                .map(board -> generateBoardInfo(query.memberId(), board, boardCountMap.getOrDefault(board.getKey(), null)))
                .toList();

        return new MissionBoardsResponse(
                getProgressCount(query.missionId()),
                MemberRanks.from(missionMembers.getMissionMembers()).getRankByMember(member).rank(),
                boards);
    }

    private MissionBoardResponse generateBoardInfo(final Long memberId, final Map.Entry<Integer, List<Member>> board,
                                                   final MissionVerification missionVerification) {
        var imageUrl = Optional.ofNullable(missionVerification)
                .map(MissionVerification::getImageUrl)
                .orElse(null);

        return MissionBoardResponse.of(memberId, board.getKey(), board.getValue(), imageUrl);
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

    private int getProgressCount(final Long missionId) {
        List<MissionVerification> missionVerifications = missionVerificationRepository.findAllByMissionIdAndDate(missionId, LocalDate.now());
        return missionVerifications.size();
    }
}