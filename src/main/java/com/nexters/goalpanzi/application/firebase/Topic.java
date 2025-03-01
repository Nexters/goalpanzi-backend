package com.nexters.goalpanzi.application.firebase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Topic {

    private static final String TOPIC_PREFIX = "/topics/missionId_";

    private final Long missionId;

    public static String generate(final Long missionId) {
        return new Topic(missionId).toString();
    }

    public static long parse(final String topic) {
        return Long.parseLong(topic.replace(TOPIC_PREFIX, ""));
    }

    @Override
    public String toString() {
        return TOPIC_PREFIX + missionId;
    }
}
