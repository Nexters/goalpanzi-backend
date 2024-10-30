package com.nexters.goalpanzi.application.firebase;

public class TopicGenerator {

    private static final String TOPIC_PREFIX = "missionId#";

    public static String getTopic(Long missionId) {
        return TOPIC_PREFIX + missionId;
    }
}
