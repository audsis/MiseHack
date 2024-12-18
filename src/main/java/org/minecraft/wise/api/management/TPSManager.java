package org.minecraft.wise.api.management;


import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.bus.Bus;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TPSManager
{
    public static volatile TPSManager INSTANCE;
    private final float[] tpsCounts;
    private volatile float ticksPerSecond;
    private volatile long lastUpdate;
    private float tickTime;

    public TPSManager() {
        this.tpsCounts = new float[10];
        this.ticksPerSecond = 20.0f;
        this.lastUpdate = -1L;

        Bus.EVENT_BUS.register(this);
    }

    public static TPSManager getInstance() {
        if (TPSManager.INSTANCE == null) {
            synchronized (TPSManager.class) {

                if (TPSManager.INSTANCE == null) {
                    TPSManager.INSTANCE = new TPSManager();
                }

            }
        }

        return TPSManager.INSTANCE;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
            onTimeUpdate();
    }

    public void reset() {
        ticksPerSecond = 20.0f;
    }

    public float getTickRate() {
        return ticksPerSecond;
    }

    public String getLastResponse() {
        long currentTime = System.currentTimeMillis();
        long responseTime = currentTime - lastUpdate;
        return String.format("%.1f", responseTime / 1000.0f);
    }

    public float getTickFactor() {
        return tickTime / 1000f;
    }

    public float getAverage() {
        float total = 0.0f;

        for (float j : tpsCounts) {
            total += j;
        }

        return total / tpsCounts.length;
    }

    public void onTimeUpdate() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdate == -1L) {
            lastUpdate = currentTime;
            return;
        }

        long timeDiff = currentTime - lastUpdate;
        tickTime = timeDiff / 20.0f;

        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }

        float tps = 1000.0f / tickTime;

        System.arraycopy(tpsCounts, 0, tpsCounts, 1, tpsCounts.length - 1);

        tpsCounts[0] = tps;
        ticksPerSecond = tps;
        lastUpdate = currentTime;
    }
}