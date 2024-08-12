package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class MissionVerifications {

    private final List<MissionVerification> missionVerifications;

    public List<MissionVerification> sortMissionVerifications(final Long memberId, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction, final List<MissionMember> missionMembers) {
        Map<Long, MissionVerification> map = missionVerifications.stream()
                .collect(Collectors.toMap(missionVerification -> missionVerification.getMember().getId(), missionVerification -> missionVerification));

        missionMembers.forEach(missionMember -> map.putIfAbsent(missionMember.getMember().getId(), null));

        return map.values().stream()
                .sorted(compareMissionVerifications(memberId, sortType, direction))
                .toList();
    }

    private static Comparator<MissionVerification> compareMissionVerifications(final Long memberId, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        return Comparator.comparing((MissionVerification missionVerification) -> missionVerification.getMember().getId().equals(memberId)).reversed()
                .thenComparing(compareMissionVerificationsByOrder(sortType, direction));
    }

    private static Comparator<MissionVerification> compareMissionVerificationsByOrder(final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        switch (sortType) {
            case MissionVerificationQuery.SortType.VERIFIED_AT:
            default:
                if (direction.isAscending()) {
                    return Comparator.comparing(MissionVerification::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                }
                return Comparator.comparing(MissionVerification::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
    }
}
