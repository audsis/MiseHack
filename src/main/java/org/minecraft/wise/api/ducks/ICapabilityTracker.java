package org.minecraft.wise.api.ducks;


public interface ICapabilityTracker {
    boolean get();

    void set(boolean state);
}