package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MissionsResponse(
        @Schema(description = "프로필", requiredMode = Schema.RequiredMode.REQUIRED)
        ProfileResponse profile,
        @Schema(description = "미션 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionResponse> missions
) {

    public static MissionsResponse of(Member member, List<MissionMember> missionMembers) {
        return new MissionsResponse(
                new ProfileResponse(member.getNickname(), member.getCharacterType()),
                missionMembers.stream()
                        .map(missionMember -> new MissionResponse(
                                missionMember.getMission().getId(),
                                missionMember.getMission().getDescription(),
                                missionMember.getMissionStatus())
                        )
                        .toList()
        );
    }
}