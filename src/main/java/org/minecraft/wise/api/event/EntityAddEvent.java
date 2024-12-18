package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.entity.Entity;

public class EntityAddEvent extends Event {
    private final Entity entity;

    public EntityAddEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}