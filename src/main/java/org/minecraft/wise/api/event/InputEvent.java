package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;

public class InputEvent extends Event {
    private final int key;
    private final int action;

    public InputEvent(int key, int action) {
        this.key = key;
        this.action = action;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }
}
