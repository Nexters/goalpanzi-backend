package com.nexters.goalpanzi.fixture;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionVerification;

public class MissionVerificationFixture {
    public static MissionVerification create(
            Mission mission,
            Member member,
            String imageUrl,
            Integer boardCount
    ) {
        return new MissionVerification(
                member,
                mission,
                imageUrl,
                boardCount
        );
    }
}
