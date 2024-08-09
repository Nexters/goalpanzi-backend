package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.mission.MemberRank;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberRankResponse(
        @Schema(description = "사용자 ID")
        Long memberId,

        @Schema(description = "사용자 닉네임")
        String nickname,

        @Schema(description = "미션 최종 순위")
        Integer rank
) {

    public static MemberRankResponse from(final MemberRank memberRank) {
        return new MemberRankResponse(
                memberRank.member().getId(),
                memberRank.member().getNickname(),
                memberRank.rank()
        );
    }
}
