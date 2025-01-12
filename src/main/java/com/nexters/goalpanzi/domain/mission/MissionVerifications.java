package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Getter
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

    public Integer size() {
        return missionVerifications.size();
    }
}
