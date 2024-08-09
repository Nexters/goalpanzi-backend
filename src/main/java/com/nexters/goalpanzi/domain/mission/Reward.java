package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Reward {
    ORANGE(1),
    CANOLA_FLOWER(3),
    DOLHARUBANG(6),
    HORSE_RIDING(9),
    HALLA_MOUNTAIN(13),
    WATERFALL(17),
    BLACK_PIG(21),
    SUNRISE(25),
    GREEN_TEA_FIELD(29),
    BEACH(31);

    private final int number;

    private static final Map<Integer, Reward> REWARD_MAP = new HashMap<>();

    static {
        for (Reward item : values()) {
            REWARD_MAP.put(item.getNumber(), item);
        }
    }

    Reward(int number) {
        this.number = number;
    }

    public static Reward of(final int number) {
        return REWARD_MAP.get(number);
    }
}
