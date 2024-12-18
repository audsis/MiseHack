package org.minecraft.wise.api.utils;

public interface Timer {

    long getTime();

    void setTime(long ms);

    default long getTimePassed() {
        return Timing.getTimePassedSince(getTime());
    }

    default boolean hasPassed(long time) {
        return Timing.getTimeOlder(getTime(), time);
    }

    default void reset() {
        setTime(Timing.getMilliseconds());
    }

    default long getStartTime() {
        return Timing.getMilliseconds();
    }

    class Multi implements Timer {
        private long time;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    class Single implements Timer {
        private long time;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
