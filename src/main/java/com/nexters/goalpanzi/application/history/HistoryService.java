package com.nexters.goalpanzi.application.history;

import com.nexters.goalpanzi.application.history.dto.response.HistoryResponse;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionStatus;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HistoryService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationRepository missionVerificationRepository;

    public HistoryResponse.CompletedMissionWrapper getMissionHistories(
            final Long memberId,
            final PageRequest pageRequest
    ) {
        // 완료한 미션 참여 멤버 목록 조회
        List<MissionMember> completedMissionMembers = getCompletedMissionMembers(memberId, pageRequest);
        Map<Long, List<MissionMember>> missionMemberMap = completedMissionMembers.stream()
                .collect(Collectors.groupingBy(missionMember -> missionMember.getMission().getId()));

        // 완료한 미션 목록 조회
        List<Long> completedMissionIds = getCompletedMissionIds(completedMissionMembers);
        List<Mission> missions = missionRepository.findAllById(completedMissionIds);

        // 미션 별 인증 목록 조회
        Map<Long, List<MissionVerification>> missionVerificationMap = getMissionVerificationMap(memberId, completedMissionIds);

        // 완료한 미션 총 개수 조회
        var totalCount = missionMemberRepository.countByMemberIdAndMissionStatus(memberId, MissionStatus.COMPLETED);

        var histories = missions.stream()
                .map(mission -> HistoryResponse.CompletedMission.of(memberId, mission, missionVerificationMap, missionMemberMap))
                .sorted(Comparator.comparing(HistoryResponse.CompletedMission::missionEndDate).reversed())
                .toList();

        return new HistoryResponse.CompletedMissionWrapper(
                totalCount,
                histories
        );

    }

    private List<Long> getCompletedMissionIds(final List<MissionMember> completedMissionMembers) {
        return completedMissionMembers.stream()
                .map(MissionMember::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, List<MissionVerification>> getMissionVerificationMap(
            final Long memberId,
            final List<Long> completedMissionIds
    ) {
        return missionVerificationRepository.findByMemberIdAndMissionIdIn(memberId, completedMissionIds)
                .stream()
                .collect(Collectors.groupingBy(missionVerification -> missionVerification.getMission().getId()));
    }

    private List<MissionMember> getCompletedMissionMembers(final Long memberId, final PageRequest pageRequest) {
        return missionMemberRepository.findByMemberIdAndMissionStatus(
                        memberId,
                        MissionStatus.COMPLETED,
                        pageRequest
                ).stream()
                .toList();
    }
}
