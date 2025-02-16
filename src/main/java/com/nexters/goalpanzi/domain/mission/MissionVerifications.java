package com.nexters.goalpanzi.domain.mission;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MissionVerifications {

    private static final int MAX_RANDOM_SIZE = 30;
    private final List<MissionVerification> missionVerifications;

    public List<String> shuffled() {
        if (missionVerifications.isEmpty()) {
            return Collections.emptyList();
        }

        // 원본 리스트가 직접 변경됨
        Collections.shuffle(missionVerifications);

        return missionVerifications.subList(0, Math.min(MAX_RANDOM_SIZE, missionVerifications.size()))
                .stream()
                .map(MissionVerification::getImageUrl)
                .toList();
    }

    public int count() {
        return missionVerifications.size();
    }
}
