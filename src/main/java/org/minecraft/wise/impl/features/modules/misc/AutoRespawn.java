package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;

public class AutoRespawn extends Module {

    public AutoRespawn() {
        super("AutoRespawn", Category.Misc);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.isDead()) {
            mc.player.requestRespawn();
        }
    }
}
