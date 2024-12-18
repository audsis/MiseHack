package org.minecraft.wise.api.management;


import org.minecraft.wise.api.wrapper.IMinecraft;

public final class TimerManager implements IMinecraft {
    public static TimerManager INSTANCE;
    private float temp;
    private float timer = 1.0f;
    private int ticks = 0;
    private int max;

    public void init() {
        if (mc.player == null) {
            reset();
        } else {
            if (ticks != 0) {
                if (ticks >= max) {
                    reset();
                    ticks = 0;
                    return;
                }
                timer = temp;
                ++ticks;
            }
        }
    }

    public void update() {
        if (ticks != 0) {
            if (ticks >= max) {
                reset();
                ticks = 0;
                return;
            }
            timer = temp;
            ++ticks;
        }
    }

    public void unload() {
        reset();
    }

    public void set(float timer) {
        this.timer = timer <= 0.0f ? 0.1f : timer;
    }

    public float getTimer() {
        return timer;
    }

    public void reset() {
        this.timer = 1.0f;
    }

    public void setFor(float timer, int ticks) {
        this.temp = timer;
        this.max = ticks;
        this.ticks = 1;
    }
}