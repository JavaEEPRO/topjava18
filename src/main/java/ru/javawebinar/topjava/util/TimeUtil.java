package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtil {
    public static boolean isBetween(LocalDateTime localDateTime, LocalTime startTime, LocalTime endTime) {
        LocalTime lt = localDateTime.toLocalTime();
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) <= 0;
    }
}
