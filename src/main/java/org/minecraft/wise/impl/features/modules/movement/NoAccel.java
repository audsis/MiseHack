package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.MoveEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.player.PlayerUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class NoAccel extends Module {

    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Max").withModes("Max", "Strict").register(this);
    private boolean pause = false;

    public NoAccel() {
        super("NoAccel", Category.Movement);
        setDescription("Removes player sprint acceleration.");
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Subscribe
    public void onMove(MoveEvent event) {
        if (pause) return;

        if (canAccelerate()) {
            double[] directionSpeed = PlayerUtils.directionSpeed(PlayerUtils.getSpeed(true));
            mc.player.setVelocity(directionSpeed[0], mc.player.getVelocity().getY(), directionSpeed[1]);
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        pause = mc.player.isSpectator() || mc.player.isSneaking();

        if (mode.getValue().equals("Strict")) {
            pause = mc.player.isSpectator() || mc.player.isSneaking() || !mc.player.isOnGround();
        }
    }


    public boolean canAccelerate() {
        return isEnabled() &&
                (mc.options.forwardKey.isPressed()
                        || mc.options.backKey.isPressed()
                        || mc.options.leftKey.isPressed()
                        || mc.options.rightKey.isPressed()) &&
                !(mc.player == null
                        || mc.player.isSneaking()
                        || mc.player.horizontalCollision
                        || mc.player.getHungerManager().getFoodLevel() <= 6f);
    }

}
