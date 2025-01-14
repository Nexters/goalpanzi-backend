package com.nexters.goalpanzi.domain.mission;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class MissionVerifications {

    private static final Random RANDOM = new Random();
    private final List<MissionVerification> missionVerifications;

    public String getRandomImageUrl() {
        if (missionVerifications.isEmpty()) {
            return null;
        }

        int randomIndex = RANDOM.nextInt(missionVerifications.size());
        return missionVerifications.get(randomIndex).getImageUrl();
    }

    public int size() {
        return missionVerifications.size();
    }
}
