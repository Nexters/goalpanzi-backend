package com.nexters.goalpanzi.application.record.dto.response;

import com.nexters.goalpanzi.domain.mission.MemberRanks;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerifications;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyRecordResponse {

    public record MyRecordWrapper(
            @Schema(description = "총 개수", requiredMode = Schema.RequiredMode.REQUIRED)
            Long totalCount,
            @Schema(description = "내 완료 미션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<MyRecord> resultList
    ) {
    }

    @Builder
    public record MyRecord(
            @Schema(description = "미션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long missionId,
            @Schema(description = "미션 이름 (목표 행동)", requiredMode = Schema.RequiredMode.REQUIRED)
            String description,
            @Schema(description = "미션 시작 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime missionStartDate,
            @Schema(description = "미션 종료 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime missionEndDate,
            @Schema(description = "나의 인증 횟수", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer myVerificationCount,
            @Schema(description = "총 인증 가능 횟수 (보드칸 개수)", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer totalVerificationCount,
            @Schema(description = "최종 등수", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer rank,
            @Schema(description = "나의 미션 인증 사진(랜덤)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String randomImageUrl,
            @Schema(description = "참여 인원 수", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer memberCount
    ) {

        public static MyRecord of(
                final Mission mission,
                final String randomImageUrl,
                final Integer myVerificationCount,
                final Integer memberCount,
                final Integer rank
        ) {
            return MyRecord.builder()
                    .missionId(mission.getId())
                    .description(mission.getDescription())
                    .missionStartDate(mission.getMissionStartDate())
                    .missionEndDate(mission.getMissionEndDate())
                    .totalVerificationCount(mission.getBoardCount())
                    .myVerificationCount(myVerificationCount)
                    .memberCount(memberCount)
                    .randomImageUrl(randomImageUrl)
                    .rank(rank)
                    .build();
        }


        public static MyRecord of(
                final Long memberId,
                final Mission mission,
                final Map<Long, List<MissionVerification>> missionVerificationMap,
                final Map<Long, List<MissionMember>> missionMemberMap
        ) {
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
        }
    }
}