package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class Yaw extends Module {

    public final Value<Boolean> silent = new ValueBuilder<Boolean>().withDescriptor("Silent").withValue(false).register(this);

    public Yaw() {
        super("Yaw", Category.Player);
        setDescription("Locks your yaw");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        float yaw = Math.round(mc.player.getYaw() / 45.0f) * 45.0f;

        if (!silent.getValue()) {
            mc.player.setYaw(yaw);
            mc.player.setHeadYaw(yaw);
        } else {
            RotationManager.INSTANCE.setRotationPoint(new RotationPoint(yaw, 0.0f, 9999, false));
        }
    }
}
