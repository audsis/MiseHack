package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;

public class LocationEvent extends Event {

    private double x;

    private double y;

    private double z;

    private float yaw;

    private float pitch;

    private boolean onGround;

    private boolean modified;

    public LocationEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setYaw(float yaw) {
        modified = true;
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        modified = true;
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        modified = true;
        this.onGround = onGround;
    }

    public boolean isModified()
    {
        return modified;
    }
}