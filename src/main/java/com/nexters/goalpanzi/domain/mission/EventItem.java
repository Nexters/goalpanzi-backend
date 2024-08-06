package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EventItem {
    ORANGE(1),
    CANOLA_FLOWER(3),
    DOLHARUBANG(6),
    HORSE_RIDING(9),
    HALLA_MOUNTAIN(13),
    WATERFALL(17),
    BLACK_PIG(21),
    SUNRISE(25),
    GREEN_TEA_FIELD(29),
    SEA(31);

    private final int number;

    private static final Map<Integer, EventItem> EVENT_ITEM_MAP = new HashMap<>();

    static {
        for (EventItem item : values()) {
            EVENT_ITEM_MAP.put(item.getNumber(), item);
        }
    }

    EventItem(int number) {
        this.number = number;
    }

    public static String of(final int number) {
        EventItem item = EVENT_ITEM_MAP.get(number);
        return (item != null) ? item.toString() : null;
    }
}
