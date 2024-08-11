package com.nexters.goalpanzi.application.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record MissionVerificationQuery(
        @NotNull Long memberId,
        @NotNull Long missionId,
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
