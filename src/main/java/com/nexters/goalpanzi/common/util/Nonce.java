package com.nexters.goalpanzi.common.util;

import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.UnauthorizedException;
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
            long nonceTime = parseDateFromString(target);

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

    private static long parseDateFromString(String target) throws ParseException {
        String[] parts = target.split(DELIMITER);
        if (parts.length != 2) {
            throw new UnauthorizedException(ErrorCode.INVALID_APPLE_TOKEN);
        }
        String dateString = parts[1];
        Date date = DATE_FORMAT.parse(dateString);
        return date.getTime();
    }
}
