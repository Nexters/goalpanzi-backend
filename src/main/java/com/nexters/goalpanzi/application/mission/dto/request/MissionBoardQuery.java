package com.nexters.goalpanzi.application.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.Sort;

public record MissionBoardQuery(
        @NotNull Long memberId,
        @NotNull Long missionId,
        SortType sortType,
        Sort.Direction direction
) {

    @Getter
    public enum SortType {
        CREATED_AT("createdAt"),
        RANDOM("random");

        private final String property;

        SortType(String property) {
            this.property = property;
        }
    }
}
