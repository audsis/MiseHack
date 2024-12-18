package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;

public class MouseEvent extends Event {
    private final int button;
    private final int action;

    public MouseEvent(int button, int action) {
        this.button = button;
        this.action = action;
    }

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }
}