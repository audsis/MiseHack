package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.TimerManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.player.PlayerUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class TickSpeed extends Module {

    private final Value<Number> timerAmount = new ValueBuilder<Number>().withDescriptor("TimerAmount").withValue(5).withRange(1, 5).register(this);
    private final Value<Number> boostTime = new ValueBuilder<Number>().withDescriptor("BoostTime").withValue(5).withRange(1, 20).register(this);
    private final Value<Number> waitTime = new ValueBuilder<Number>().withDescriptor("WaitTime").withValue(10).withRange(1, 30).register(this);
    private final Value<Boolean> disableBoost = new ValueBuilder<Boolean>().withDescriptor("Disable").withValue(false).register(this);
    private int boosted;

    public TickSpeed() {
        super("TickSpeed", Category.Movement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (boosted != waitTime.getValue().intValue())
            ++boosted;

        if (boosted != waitTime.getValue().intValue())
            return;

        if (PlayerUtils.movement()) {
            TimerManager.INSTANCE.setFor(timerAmount.getValue().floatValue(), boostTime.getValue().intValue());

            if (disableBoost.getValue())
                setEnabled(false);

            boosted = 0;
        }
    }

    @Override
    public String getHudInfo() {
        return "" + boosted;
    }
}
