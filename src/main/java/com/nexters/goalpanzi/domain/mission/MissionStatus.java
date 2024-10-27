package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.common.time.TimeUtil;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.exception.ErrorCode.UNKNOWN_MISSION;

@Getter
public enum MissionStatus {

    // 미션 시작 전
    CREATED("생성"),

    // 미션 진행 기간,
    CANCELED("취소"),
    IN_PROGRESS("진행중"),
    DELETED("삭제"),

    // 미션 종료일 이후
    PENDING_COMPLETION("종료 대기"),
    COMPLETED("완료");

    private final String description;

    MissionStatus(final String description) {
        this.description = description;
    }

    public static MissionStatus fromMission(
            final Mission mission,
            final Integer currentMemberCount,
            final MissionMember missionMember
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime missionStart = mission.getMissionUploadStartDateTime();
        LocalDateTime missionEnd = mission.getMissionUploadEndDateTime();

        if (now.isBefore(missionStart)) {
            return CREATED;
        }

        if (mission.isMissionPeriod() && currentMemberCount <= 1) {
            return CANCELED;
        }

        if (mission.isMissionPeriod() && currentMemberCount > 1) {
            return IN_PROGRESS;
        }

        if (now.isAfter(missionEnd) && !missionMember.getCheckCompleted()) {
            return PENDING_COMPLETION;
        }

        if (now.isAfter(missionEnd) && missionMember.getCheckCompleted()) {
            return COMPLETED;
        }

        throw new IllegalArgumentException(UNKNOWN_MISSION.getMessage(mission, missionMember));
    }
}
