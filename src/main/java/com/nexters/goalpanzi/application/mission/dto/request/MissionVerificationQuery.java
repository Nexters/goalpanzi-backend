package com.nexters.goalpanzi.application.mission.dto.request;

import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record MissionVerificationQuery(
        Long memberId,
        Long missionId,
        LocalDate date,
        SortType sortType,
        Sort.Direction direction
) {

    public enum SortType {
        CREATED_AT,
    }
}
