package org.minecraft.wise.impl.features.modules.movement;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.TimerManager;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class Timer extends Module {

    private final Value<Number> amount = new ValueBuilder<Number>().withDescriptor("Amount").withValue(1.08888).withRange(0.1, 20).register(this);

    public Timer() {
        super("Timer", Category.Movement);
    }

    @Override
    public void onEnable() {
        TimerManager.INSTANCE.set(amount.getValue().floatValue());
    }
}
