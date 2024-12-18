package org.minecraft.wise.api.utils;

import java.util.concurrent.TimeUnit;

public class Timing {
    public static final long NANOS = TimeUnit.MILLISECONDS.toNanos(1L);

    public static long getMilliseconds() {
        return System.nanoTime() / NANOS;
    }

    public static long getTimePassedSince(long number) {
        return getMilliseconds() - number;
    }

    public static boolean getTimeOlder(long time, long ms) {
        return getTimePassedSince(time) >= ms;
    }
}
