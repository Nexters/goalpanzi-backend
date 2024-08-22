package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerificationView;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MissionVerificationResponseSorter {

    private final MissionVerificationViewRepository missionVerificationViewRepository;

    public List<MissionVerificationResponse> sort(
            final Member me,
            final MissionVerificationQuery.SortType sortType,
            final Sort.Direction direction,
            final List<MissionVerification> missionVerifications,
            final List<MissionMember> missionMembers
    ) {
        List<MissionVerificationResponse> responses = new ArrayList<>();
        Map<Long, MissionVerification> verifications = missionVerifications.stream()
                .collect(Collectors.toMap(
                        missionVerification -> missionVerification.getMember().getId(),
                        missionVerification -> missionVerification));

        missionMembers.forEach(missionMember -> {
            Member member = missionMember.getMember();
            Optional<MissionVerification> verification = Optional.ofNullable(verifications.get(member.getId()));
            MissionVerificationResponse response = createResponseOfMissionMember(member, me, verification);
            responses.add(response);
        });

        responses.sort(compareResponses(me.getNickname(), sortType, direction));
        return responses;
    }

    private MissionVerificationResponse createResponseOfMissionMember(final Member member, final Member viewer, final Optional<MissionVerification> missionVerification) {
        if (missionVerification.isEmpty()) {
            return MissionVerificationResponse.of(member, Optional.empty(), Optional.empty());
        }
        MissionVerificationView missionVerificationView = missionVerificationViewRepository.getMissionVerificationView(missionVerification.get().getId(), viewer.getId());
        return MissionVerificationResponse.of(member, missionVerification, Optional.ofNullable(missionVerificationView));
    }

    private Comparator<MissionVerificationResponse> compareResponses(final String myNickname, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        return myVerificationFirst(myNickname)
                .thenComparing(unviewedVerificationFirst())
                .thenComparing(compareResponsesByOrder(sortType, direction));
    }

    private Comparator<MissionVerificationResponse> myVerificationFirst(final String myNickname) {
        return Comparator.comparing((MissionVerificationResponse missionVerificationResponse) -> missionVerificationResponse.nickname().equals(myNickname)).reversed();
    }

    private Comparator<MissionVerificationResponse> unviewedVerificationFirst() {
        return Comparator.comparing(MissionVerificationResponse::viewedAt, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private Comparator<MissionVerificationResponse> compareResponsesByOrder(final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        switch (sortType) {
            case MissionVerificationQuery.SortType.VERIFIED_AT:
            default:
                if (direction.isAscending()) {
                    return Comparator.comparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                }
                return Comparator.comparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
    }
}
