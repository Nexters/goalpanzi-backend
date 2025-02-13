package com.nexters.goalpanzi.application.history;

import com.nexters.goalpanzi.application.history.dto.response.HistoryResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
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

import java.util.Collections;
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
    private final MemberRepository memberRepository;

    public HistoryResponse.CompletedMissionWrapper getMissionHistories(
            final Long memberId,
            final PageRequest pageRequest
    ) {
        // 1. 완료한 미션 참여 멤버 목록 조회
        List<MissionMember> completedMissionMembers = getCompletedMissionMembers(memberId, pageRequest);
        Map<Long, List<MissionMember>> missionMemberMap = completedMissionMembers.stream()
                .collect(Collectors.groupingBy(missionMember -> missionMember.getMission().getId()));

        // 2. 완료한 미션 목록 조회
        List<Long> completedMissionIds = getCompletedMissionIds(completedMissionMembers);
        List<Mission> missions = missionRepository.findAllById(completedMissionIds);

        // 3. 미션 별 인증 목록 조회
        Map<Long, List<MissionVerification>> missionVerificationMap = getMissionVerificationMap(memberId, completedMissionIds);

        // 4. 완료한 미션 총 개수 조회
        var totalCount = missionMemberRepository.countByMemberIdAndMissionStatus(memberId, MissionStatus.COMPLETED);

        var histories = missions.stream()
                .map(mission -> HistoryResponse.CompletedMission.of(
                        memberId, mission, missionVerificationMap.getOrDefault(mission.getId(), Collections.emptyList())
                        , missionMemberMap.getOrDefault(mission.getId(), Collections.emptyList())))
                .sorted(Comparator.comparing(HistoryResponse.CompletedMission::missionEndDate).reversed())
                .toList();

        return new HistoryResponse.CompletedMissionWrapper(
                totalCount,
                histories
        );
    }

    public HistoryResponse.VerificationWrapper getMissionVerificationHistories(
            final Long missionId,
            final Long memberId,
            final PageRequest pageRequest
    ) {
        Mission mission = missionRepository.getMission(missionId);
        Member member = memberRepository.getMember(memberId);
        var missionVerifications = missionVerificationRepository.findByMemberIdAndMissionIdAndDeletedAtIsNull(memberId, missionId, pageRequest)
                .stream()
                .map(it -> new HistoryResponse.Verification(
                        it.getImageUrl(),
                        it.getCreatedAt()))
                .toList();
        long totalCount = missionVerificationRepository.countByMemberIdAndMissionIdAndDeletedAtIsNull(memberId, missionId);

        return HistoryResponse.VerificationWrapper.builder()
                .totalCount(totalCount)
                .nickname(member.getNickname())
                .missionId(mission.getId())
                .description(mission.getDescription())
                .verifications(missionVerifications)
                .build();
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
