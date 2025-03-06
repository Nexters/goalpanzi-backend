package com.nexters.goalpanzi.application.history;

import com.nexters.goalpanzi.application.history.dto.response.HistoryResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.MemberRanks;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionStatus;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerifications;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
@Slf4j
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
        var myCompletedMissionSlice = missionMemberRepository.findByMemberIdAndMissionStatus(memberId, MissionStatus.COMPLETED, pageRequest);
        List<MissionMember> completedMissionMembers = getParticipatedMissionMembers(myCompletedMissionSlice);
        Map<Long, List<MissionMember>> missionMemberMap = completedMissionMembers.stream()
                .collect(Collectors.groupingBy(missionMember -> missionMember.getMission().getId()));
        var totalMissionCount = missionMemberRepository.countByMemberIdAndMissionStatus(memberId, MissionStatus.COMPLETED);

        // 2. 완료한 미션 목록 조회
        List<Long> completedMissionIds = getCompletedMissionIds(completedMissionMembers);
        List<Mission> missions = missionRepository.findAllByIdIn(completedMissionIds);

        // 3. 미션 별 인증 목록 조회
        Map<Long, List<MissionVerification>> missionVerificationMap = getMissionVerificationMap(memberId, completedMissionIds);

        var histories = missions.stream()
                .filter(mission -> missionMemberMap.get(mission.getId()) != null)
                .map(mission -> {
                            var missionVerifications = missionVerificationMap.getOrDefault(mission.getId(), Collections.emptyList());
                            var missionMembers = missionMemberMap.getOrDefault(mission.getId(), Collections.emptyList());
                            return convertToCompletedMission(memberId, mission, missionVerifications, missionMembers);
                        }
                )
                .sorted(Comparator.comparing(HistoryResponse.CompletedMission::missionEndDate).reversed())
                .toList();

        return new HistoryResponse.CompletedMissionWrapper(
                totalMissionCount,
                myCompletedMissionSlice.hasNext(),
                histories
        );
    }

    private HistoryResponse.CompletedMission convertToCompletedMission(
            final Long memberId, final Mission mission,
            final List<MissionVerification> missionVerifications,
            final List<MissionMember> missionMembers) {
        return HistoryResponse.CompletedMission.of(
                memberId,
                mission,
                new MissionVerifications(missionVerifications),
                missionMembers.stream()
                        .map(mm -> new HistoryResponse.MissionMemberInfo(
                                mm.getMember().getId(),
                                mm.getMember().getNickname(),
                                mm.getMember().getCharacterType()
                        )).toList(),
                MemberRanks.from(missionMembers)
        );
    }

    public HistoryResponse.VerificationWrapper getMissionVerificationHistories(
            final Long missionId,
            final Long memberId,
            final PageRequest pageRequest
    ) {
        Mission mission = missionRepository.getMission(missionId);
        Member member = memberRepository.getMember(memberId);
        var missionVerificationSlice = missionVerificationRepository.findByMemberIdAndMissionId(memberId, missionId, pageRequest);
        var missionVerifications = missionVerificationSlice.stream()
                .map(it -> new HistoryResponse.Verification(
                        it.getImageUrl(),
                        it.getCreatedAt()))
                .toList();

        return HistoryResponse.VerificationWrapper.builder()
                .hasNext(missionVerificationSlice.hasNext())
                .nickname(member.getNickname())
                .missionId(mission.getId())
                .description(mission.getDescription())
                .verifications(missionVerifications)
                .build();
    }

    private List<Long> getCompletedMissionIds(final List<MissionMember> completedMissionMembers) {
        return completedMissionMembers.stream()
                .map(it -> it.getMission().getId())
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

    private List<MissionMember> getParticipatedMissionMembers(Slice<MissionMember> completedMissions) {
        var missionIds = completedMissions.stream()
                .map(it -> it.getMission().getId())
                .toList();

        return missionMemberRepository.findAllByMissionIdIn(missionIds);
    }
}
