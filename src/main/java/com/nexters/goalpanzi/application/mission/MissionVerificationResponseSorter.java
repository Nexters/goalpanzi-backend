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
            final Member member,
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
            Member member1 = missionMember.getMember();
            Optional<MissionVerification> verification = Optional.ofNullable(verifications.get(member1.getId()));
            MissionVerificationResponse response = createResponse(member1, verification);
            responses.add(response);
        });

        responses.sort(compareResponses(member.getNickname(), sortType, direction));
        return responses;
    }

    private MissionVerificationResponse createResponse(final Member member, final Optional<MissionVerification> optionalMissionVerification) {
        return optionalMissionVerification
                .map(missionVerification -> {
                    MissionVerificationView missionVerificationView = missionVerificationViewRepository.getMissionVerificationView(missionVerification.getId(), member.getId());
                    return MissionVerificationResponse.of(member, Optional.of(missionVerification), Optional.ofNullable(missionVerificationView));
                })
                .orElseGet(() -> MissionVerificationResponse.of(member, Optional.empty(), Optional.empty()));
    }

    private Comparator<MissionVerificationResponse> compareResponses(final String nickname, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        return myVerificationFirst(nickname)
                .thenComparing(unviewedVerificationFirst())
                .thenComparing(compareResponsesByOrder(sortType, direction));
    }

    private Comparator<MissionVerificationResponse> myVerificationFirst(final String nickname) {
        return Comparator.comparing((MissionVerificationResponse missionVerificationResponse) -> missionVerificationResponse.nickname().equals(nickname)).reversed();
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
