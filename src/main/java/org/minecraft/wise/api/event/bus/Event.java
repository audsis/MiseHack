package org.minecraft.wise.api.event.bus;

import lombok.Getter;

@Getter
public class Event {
    private boolean cancelled;

    public void cancel() {
        cancelled = true;
    }
}