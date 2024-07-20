package com.nexters.goalpanzi.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class Nonce {

    private static final String PREFIX = "goalpanzi";
    private static final String DELIMITER = "_";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String generate() {
        return PREFIX + DELIMITER + DATE_FORMAT.format(new Date());
    }

    public static boolean isValid(String target) {
        try {
            if (target == null || !target.contains(DELIMITER)) {
                return false;
            }
            var nonceTime = DATE_FORMAT.parse(target.split(DELIMITER)[1]).getTime();

            return isIssuedInThreeMinutes(nonceTime);

        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static boolean isIssuedInThreeMinutes(long nonceTime) {
        var currentTime = new Date().getTime();
        var threeMinutesInMillis = 3 * 60 * 1000;
        return currentTime - nonceTime <= threeMinutesInMillis;
    }
}
