package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class RenderPlayerModelEvent extends Event {
    private final AbstractClientPlayerEntity entity;
    private float yaw;
    private float pitch;
    public RenderPlayerModelEvent(AbstractClientPlayerEntity entity) {
        this.entity = entity;
    }

    public AbstractClientPlayerEntity getEntity() {
        return entity;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}