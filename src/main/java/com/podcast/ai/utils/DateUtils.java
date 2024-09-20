package com.podcast.ai.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@UtilityClass
public class DateUtils {

    public static Long toSeconds(LocalDateTime dateTime) {
        if(dateTime == null) return null;
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toInstant().getEpochSecond();
    }
}
