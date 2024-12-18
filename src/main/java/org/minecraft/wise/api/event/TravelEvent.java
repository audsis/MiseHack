package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.util.math.Vec3d;

public class TravelEvent extends Event {
    private final Vec3d movementInput;

    public TravelEvent(Vec3d movementInput) {
        this.movementInput = movementInput;
    }

    public Vec3d getMovementInput() {
        return movementInput;
    }
}