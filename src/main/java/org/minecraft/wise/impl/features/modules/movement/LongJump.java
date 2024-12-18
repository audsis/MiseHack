package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.event.TravelEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.ducks.IEntity;
import org.minecraft.wise.mixin.mixins.access.ILivingEntity;

public class LongJump extends Module {

    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Grim").withModes("Normal", "Grim").register(this);
    private final Timer timer = new Timer.Single();
    private boolean swapped;

    public LongJump() {
        super("LongJump", Category.Movement);
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (mode.getValue().equals("Grim")) {

            if (timer.hasPassed(500)) {

                return;
            }

//            if (PlayerUtils.canFallFlying() && mc.player.getVelocity().y < 0) {
//                PlayerUtils.startFallFlying(true);
//            }

            ((ILivingEntity) mc.player).setLastJumpCooldown(0);
        }
    }

    @Subscribe
    public void onTravel(TravelEvent event) {
        if (isSwapped()) {
            ((IEntity) mc.player).setFlag(7, false);
        }
    }

    private boolean isSwapped() {
        return swapped;
    }

}
