package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.VerificationOrderBy;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record MissionVerificationsResponse(
        @Schema(description = "미션 인증 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionVerificationResponse> missionVerifications
) {

    public static MissionVerificationsResponse of(final Long memberId, final VerificationOrderBy orderBy, final List<MissionMember> missionMembers, final List<MissionVerification> verifications) {

        return new MissionVerificationsResponse(
                missionMembers.stream()
                        .sorted(compareMissionMembers(memberId, orderBy))
                        .map(missionMember -> MissionVerificationResponse.of(
                                missionMember.getMember(),
                                findVerification(missionMember.getMember(), verifications)
                        ))
                        .toList()
        );
    }

    private static Comparator<MissionMember> compareMissionMembers(final Long memberId, final VerificationOrderBy orderBy) {
        return Comparator.comparing((MissionMember missionMember) -> missionMember.getId().equals(memberId)).reversed()
                .thenComparing(compareMissionMembersByOrder(orderBy));
    }

    private static Comparator<MissionMember> compareMissionMembersByOrder(final VerificationOrderBy orderBy) {
        switch (orderBy) {
            case VerificationOrderBy.CREATED_AT:
                Comparator.comparing(MissionMember::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case VerificationOrderBy.CREATED_AT_DESC:
            default:
                return Comparator.comparing(MissionMember::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
    }

    private static Optional<MissionVerification> findVerification(final Member member, final List<MissionVerification> verifications) {
        return verifications.stream().filter(verification -> verification.getMember().equals(member)).findFirst();
    }
}
