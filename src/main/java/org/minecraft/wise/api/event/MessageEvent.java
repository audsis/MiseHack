package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;

public class MessageEvent extends Event {
    public String message;

    public MessageEvent(String message) {
        this.message = message;
    }
}