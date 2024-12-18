package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.client.util.math.MatrixStack;

public class Render3dEvent extends Event {
    private final MatrixStack matrices;
    private final float delta;

    public Render3dEvent(MatrixStack matrices, float delta) {
        this.matrices = matrices;
        this.delta = delta;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    public float getDelta() {
        return delta;
    }
}
