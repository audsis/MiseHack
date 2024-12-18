package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Step extends Module {
    final Value<Number> height = new ValueBuilder<Number>().withDescriptor("Height").withValue(2.1).withRange(0.1, 7).register(this);
    final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Vanilla").withModes("Vanilla", "Normal").register(this);
    private final Timer timer = new Timer.Single();

    public Step() {
        super("Step", Category.Movement);
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        setStepHeight(0.6f);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (isEnabled()) {
            if (timer.hasPassed(250))
                setStepHeight(height.getValue().floatValue());

            if (mode.getValue().equals("Normal")) {
                double stepHeight = mc.player.getY() - mc.player.prevY;
                double[] offsets = getOffset(stepHeight);

                if (stepHeight <= 0.5 || stepHeight > height.getValue().floatValue())
                    return;

                if (offsets != null && offsets.length > 1) {
                    for (double offset : offsets) {
                        send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + offset, mc.player.getZ(), false));
                    }
                }
                timer.reset();
            }
        }
    }

    public double[] getOffset(double height) {
        if (height == 0.75) {
            return new double[]{
                    0.42, 0.753, 0.75
            };
        }
        else if (height == 0.8125) {
            return new double[]{
                    0.39, 0.7, 0.8125
            };
        }
        else if (height == 0.875) {
            return new double[]{
                    0.39, 0.7, 0.875
            };
        } else if (height == 1) {
            return new double[]{
                    0.42, 0.753, 1
            };
        } else if (height == 1.5) {
            return new double[]{
                    0.42, 0.75, 1.0, 1.16, 1.23, 1.2
            };
        } else if (height == 2) {
            return new double[]{
                    0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43
            };
        } else if (height == 2.5) {
            return new double[]{
                    0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907
            };
        }
        return null;
    }

    private void setStepHeight(float shit) {
        mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(shit);
    }
}
