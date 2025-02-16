package com.nexters.goalpanzi.application.history.dto.response;

import com.nexters.goalpanzi.domain.member.CharacterType;
import com.nexters.goalpanzi.domain.mission.MemberRanks;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionVerifications;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryResponse {

    public record CompletedMissionWrapper(
            @Schema(description = "다음 페이지 존재 여부", requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean hasNext,
            @Schema(description = "내 미션 히스토리 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<CompletedMission> resultList
    ) {
    }

    @Builder
    public record CompletedMission(
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
            @Schema(description = "나의 미션 인증 사진(랜덤) 리스트 (최대 30개)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            List<String> randomImageUrlList,
            @Schema(description = "참여 인원 수", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer memberCount,
            @Schema(description = "참여 인원 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<MissionMemberInfo> missionMembers
    ) {

        public static CompletedMission of(
                final Mission mission,
                final List<String> randomImageUrlList,
                final Integer myVerificationCount,
                final Integer rank,
                final List<MissionMemberInfo> missionMembers
        ) {
            return CompletedMission.builder()
                    .missionId(mission.getId())
                    .description(mission.getDescription())
                    .missionStartDate(mission.getMissionStartDate())
                    .missionEndDate(mission.getMissionEndDate())
                    .totalVerificationCount(mission.getBoardCount())
                    .myVerificationCount(myVerificationCount)
                    .randomImageUrlList(randomImageUrlList)
                    .rank(rank)
                    .memberCount(missionMembers.size())
                    .missionMembers(missionMembers)
                    .build();
        }

        public static CompletedMission of(
                final Long memberId,
                final Mission mission,
                final MissionVerifications missionVerifications,
                final List<MissionMemberInfo> missionMembers,
                final MemberRanks memberRanks
        ) {

            return CompletedMission.of(
                    mission,
                    missionVerifications.shuffled(),
                    missionVerifications.count(),
                    memberRanks.getRankByMemberId(memberId).rank(),
                    missionMembers
            );
        }
    }

    @Builder
    public record VerificationWrapper(
            @Schema(description = "다음 페이지 존재 여부", requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean hasNext,
            @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
            String nickname,
            @Schema(description = "미션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long missionId,
            @Schema(description = "미션 이름 (목표 행동)", requiredMode = Schema.RequiredMode.REQUIRED)
            String description,
            @Schema(description = "미션 인증 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<Verification> verifications
    ) {

    }

    public record Verification(
            @Schema(description = "인증 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
            String imageUrl,
            @Schema(description = "인증 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime date
    ) {

    }

    public record MissionMemberInfo(
            @Schema(description = "참여자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long memberId,
            @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
            String nickname,
            @Schema(description = "캐릭터 타입", requiredMode = Schema.RequiredMode.REQUIRED)
            CharacterType characterType
    ) {

    }
}
