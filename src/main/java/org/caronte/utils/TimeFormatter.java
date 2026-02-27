package org.caronte.utils;

public class TimeFormatter {

    public static long getDays(long millis) {
        return millis / (1000 * 60 * 60 * 24);
    }

    public static long getHours(long millis) {
        return (millis / (1000 * 60 * 60)) % 24;
    }

    public static long getMinutes(long millis) {
        return (millis / (1000 * 60)) % 60;
    }
}