package org.minecraft.wise.api.utils.rotation;

public final class RotationPoint {
    private float yaw, pitch;

    private int priority;

    private boolean instant;

    public RotationPoint(float yaw, float pitch, int priority, boolean instant)
    {
        this.yaw = yaw;
        this.pitch = pitch;
        this.priority = priority;
        this.instant = instant;
    }

    public float getYaw() {
        return yaw;
    }
    public float getPitch() {
        return pitch;
    }
    public int getPriority() {
        return priority;
    }
    public boolean getInstant() {
        return instant;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public void setInstant(boolean instant) {
        this.instant = instant;
    }
}