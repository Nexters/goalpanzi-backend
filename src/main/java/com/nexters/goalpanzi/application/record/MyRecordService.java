package com.nexters.goalpanzi.application.record;

import com.nexters.goalpanzi.application.record.dto.response.MyRecordResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MyRecordService {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationRepository missionVerificationRepository;

    public MyRecordResponse.MyRecordWrapper getMyRecordList(
            final Long memberId,
            final PageRequest pageRequest
    ) {

        List<MissionMember> completedMissionMembers = getCompletedMissionMembers(memberId, pageRequest);
        List<Long> completedMissionIds = completedMissionMembers.stream()
                .map(MissionMember::getId)
                .collect(Collectors.toList());

        List<Mission> missions = missionRepository.findAllById(completedMissionIds);

        Map<Long, List<MissionVerification>> missionVerificationMap = getMissionVerificationMap(memberId, completedMissionIds);
        Map<Long, List<MissionMember>> missionMemberMap = completedMissionMembers.stream()
                .collect(Collectors.groupingBy(missionMember -> missionMember.getMission().getId()));
        var totalCount = missionMemberRepository.countByMemberIdAndMissionStatus(memberId, MissionStatus.COMPLETED);

        var myRecordList = missions.stream()
                .map(mission -> {
                    MissionVerifications missionVerifications = new MissionVerifications(missionVerificationMap.get(mission.getId()));
                    List<MissionMember> missionMembers = missionMemberMap.get(mission.getId());
                    MemberRanks memberRanks = MemberRanks.from(missionMembers);
                    return MyRecordResponse.MyRecord.of(
                            mission,
                            missionVerifications.getRandomImageUrl(),
                            missionVerifications.size(),
                            missionMembers.size(),
                            memberRanks.getRankByMemberId(memberId).rank()
                    );
                })
                .toList();

        return new MyRecordResponse.MyRecordWrapper(
                totalCount,
                myRecordList
        );

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
