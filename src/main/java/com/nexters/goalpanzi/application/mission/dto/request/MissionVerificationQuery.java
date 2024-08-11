package com.nexters.goalpanzi.application.mission.dto.request;

import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record MissionVerificationQuery(
        Long memberId,
        Long missionId,
        LocalDate date,
        SortType sortType,
        Sort.Direction direction
) {

    @Getter
    public enum SortType {
        CREATED_AT("createdAt");

        private final String property;

        SortType(String property) {
            this.property = property;
        }
    }
}
