package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.client.input.Input;


public class KeyboardEvent extends Event {

    private final Input input;

    public KeyboardEvent(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return input;
    }
}
