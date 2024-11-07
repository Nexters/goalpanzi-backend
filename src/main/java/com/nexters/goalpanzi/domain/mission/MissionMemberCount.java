package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

@Getter
public enum MissionMemberCount {
    MIN(2);

    private final int count;

    MissionMemberCount(final int count) {
        this.count = count;
    }
}
