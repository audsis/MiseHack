package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.util.math.Vec3d;

public class FastFall extends Module {

    public final Value<Number> height = new ValueBuilder<Number>().withDescriptor("Height").withValue(1.0f).withRange(0.0f, 10.0f).register(this);
    public final Value<Number> speed = new ValueBuilder<Number>().withDescriptor("Speed").withValue(1.0f).withRange(0.0f, 10.0f).register(this);

    public FastFall() {
        super("FastFall", Category.Movement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (mc.options.jumpKey.isPressed())
            return;

        if (mc.player.isOnGround()) {
            mc.player.setVelocity(new Vec3d(mc.player.getVelocity().x, -speed.getValue().intValue(), mc.player.getVelocity().z));
        }
    }
}
