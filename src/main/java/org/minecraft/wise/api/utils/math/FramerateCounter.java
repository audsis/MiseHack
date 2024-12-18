package org.minecraft.wise.api.utils.math;

import java.util.ArrayList;
import java.util.List;

public class FramerateCounter {
    public static FramerateCounter INSTANCE;
    final List<Long> records;

    public FramerateCounter() {
        records = new ArrayList<>();
    }

    public void recordFrame() {
        long c = System.currentTimeMillis();
        records.add(c);
        records.removeIf(aLong -> aLong + 1000L < System.currentTimeMillis());
    }

    public int getFps() {
        return records.size();
    }
}
