package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class AntiAFK extends Module {

    private final Value<Boolean> spin = new ValueBuilder<Boolean>().withDescriptor("Spin").withValue(true).register(this);
    private final Value<Number> speed = new ValueBuilder<Number>().withDescriptor("Speed").withValue(5).withRange(1, 30).register(this);
    private float oldYaw;

    public AntiAFK() {
        super("AntiAFK", Category.Player);
        setDescription("Prevents AFK kicks on servers.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (NullUtils.nullCheck())
            return;

        oldYaw = mc.player.getYaw();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (NullUtils.nullCheck())
            return;

        mc.player.setYaw(oldYaw);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        int yaw = 0;

        yaw += speed.getValue().intValue();

        if (spin.getValue()) {
            mc.player.setYaw(yaw);
        }
    }

}
