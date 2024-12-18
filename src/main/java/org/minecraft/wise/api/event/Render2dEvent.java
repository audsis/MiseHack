package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.client.gui.DrawContext;

public class Render2dEvent extends Event {
    private final DrawContext context;
    private final float delta;

    public Render2dEvent(DrawContext context, float delta) {
        this.context = context;
        this.delta = delta;
    }

    public DrawContext getContext() {
        return context;
    }

    public float getDelta() {
        return delta;
    }
}
