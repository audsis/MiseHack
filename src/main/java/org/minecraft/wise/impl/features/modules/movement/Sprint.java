package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;

public class Sprint extends Module {

    public static Sprint INSTANCE;
    Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Rage").withModes("Legit", "Rage").register(this);
    public final Value<Boolean> grim = new ValueBuilder<Boolean>().withDescriptor("Grim").withValue(false).register(this);

    public Sprint() {
        super("Sprint", Feature.Category.Movement);
        INSTANCE = this;

    }

    @Subscribe
    public void onUpdate(TickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }

        if (grim.getValue() && mode.getValue().contains("Rage") && canRageSprint()) {
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();

            if (mc.player.input.pressingBack)
                yaw += 180;
            else if (mc.player.input.pressingRight)
                yaw += 90;
            else if (mc.player.input.pressingLeft)
                yaw -= 90;

            yaw = (yaw % 360 + 360) % 360;

            RotationManager.INSTANCE.setRotationPoint(new RotationPoint(yaw, pitch, 1, false));
        }

        if (mode.getValue().contains("Rage")) {
            if (canRageSprint()) {
                mc.player.setSprinting(true);
            }
        } else {
            if (mc.options.forwardKey.isPressed() && !(mc.player.isSneaking() || mc.player.isUsingItem() || mc.player.horizontalCollision || mc.player.getHungerManager().getFoodLevel() <= 6f) && mc.currentScreen == null) {
                mc.player.setSprinting(true);
            }
        }
    }

    public boolean canRageSprint() {
        boolean king = isEnabled() && mode.getValue().contains("Rage") &&
                (mc.options.forwardKey.isPressed()
                        || mc.options.backKey.isPressed()
                        || mc.options.leftKey.isPressed()
                        || mc.options.rightKey.isPressed()) &&
                !(mc.player == null
                        || mc.player.isSneaking()
                        || mc.player.horizontalCollision
                        || mc.player.getHungerManager().getFoodLevel() <= 6f);

        if (grim.getValue())
            return king && mc.player.isOnGround();

        return king;
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }
}
