package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.entity.MovementType;

public class EntityMovementEvent extends Event {

    private final MovementType type;

    private double x;

    private double y;

    private double z;

    private final double horizontalLength;

    public EntityMovementEvent(MovementType type, double x, double y, double z, double horizontalLength) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontalLength = horizontalLength;
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

    public MovementType getType() {
        return type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getHorizontalLength() {
        return horizontalLength;
    }
}
