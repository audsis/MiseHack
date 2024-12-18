package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.MoveEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.TimerManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.player.PlayerUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

@SuppressWarnings({"unused"})
public class Speed extends Module {

    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Strafe").withModes("Strafe", "Jump", "Grim", "StrictFast").register(this);
    private final Value<Boolean> timer = new ValueBuilder<Boolean>().withDescriptor("Timer").withValue(true).register(this);
    private final Value<Boolean> inLiquids = new ValueBuilder<Boolean>().withDescriptor("InLiquids").withValue(true).register(this);
    double speed = 0.0;
    double distance = 0.0;
    int stage = 0;
    boolean boost = false;
    double strictTicks;

    public Speed() {
        super("Speed", Category.Movement);
        setDescription("Lets you go faster");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        TimerManager.INSTANCE.reset();
        stage = 1;
        speed = 0.0f;
        distance = 0.0;
        boost = false;
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Subscribe
    public void onGang(MoveEvent event) {

        if (NullUtils.nullCheck() || mc.player.isSpectator() || !PlayerUtils.movement()) return;

        if (!inLiquids.getValue() && (mc.player.isInsideWaterOrBubbleColumn() || mc.player.isInLava())) return;

        event.cancel();

        float defaultSpeed = 0.2873f;
        if (mode.getValue().equals("Strafe")) {

            if (mc.player.fallDistance <= 5.0 && PlayerUtils.movement()) {
                double velocityY = mc.player.getVelocity().y;

                if (stage == 1) {
                    speed *= 1.35f * PlayerUtils.calcEffects(2873.0) - 0.01;
                } else if (stage == 2) {
                    velocityY = 0.3999999463558197f;

                    if (boost) {
                        speed *= 1.6835;
                    } else {
                        speed *= 1.395;
                    }

                    boost = !boost;
                } else if (stage == 3) {
                    speed = distance - 0.66 * (distance - PlayerUtils.calcEffects(0.2873));
                } else {
                    if ((!mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0, mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision) && stage > 0) {
                        stage = 1;
                    }

                    speed = distance - distance / 159.0;
                }

                speed = Math.min(speed, PlayerUtils.calcEffects(10.0));
                speed = Math.max(speed, PlayerUtils.calcEffects(0.2873));

                PlayerUtils.strafe(event, speed);

                mc.player.setVelocity(0.0, velocityY, 0.0);
                event.setY(velocityY);
                stage++;

            }
        } else if (mode.getValue().equals("StrictFast")) {
            if (mc.player.fallDistance <= 5.0 && PlayerUtils.movement()) {
                double velocityY = mc.player.getVelocity().y;

                if (stage == 1) {
                    speed *= 1.35f * PlayerUtils.calcEffects(2873.0) - 0.01;
                } else if (stage == 2) {
                    velocityY = 0.4000000054314141413434141341431f;

                    if (boost) {
                        speed *= 1.6835;
                    } else {
                        speed *= 1.408;
                    }

                    boost = !boost;
                } else if (stage == 3) {
                    speed = distance - 0.66 * (distance - PlayerUtils.calcEffects(0.2873));
                } else {
                    if ((!mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0, mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision) && stage > 0) {
                        stage = 1;
                    }

                    speed = distance - distance / 159.0;
                }

                strictTicks++;

                speed = Math.min(speed, PlayerUtils.calcEffects(10.0));
                speed = Math.max(speed, PlayerUtils.calcEffects(0.2873));

                speed = Math.min(speed, strictTicks > 25 ? 0.465 : 0.44);

                if (strictTicks > 50) {
                    strictTicks = 0;
                }

                PlayerUtils.strafe(event, speed);

                mc.player.setVelocity(0.0, velocityY, 0.0);
                event.setY(velocityY);
                stage++;

            }
        }
    }

    @Subscribe
    public void onUpdate(TickEvent event) {

        if (timer.getValue()) {
            TimerManager.INSTANCE.set(1.0888f);
        } else {
            TimerManager.INSTANCE.reset();
        }

        double diffX = mc.player.getX() - mc.player.prevX;
        double diffZ = mc.player.getZ() - mc.player.prevZ;

        distance = Math.sqrt(diffX * diffX + diffZ * diffZ);

        if (mode.getValue().equals("Jump")) {
            if (!mc.player.isOnGround() || !PlayerUtils.movement()) return;

            mc.player.jump();
        }

    }
}