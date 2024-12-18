package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import net.minecraft.client.option.KeyBinding;

public class AutoWalk extends Module {
    public static AutoWalk INSTANCE;

    public AutoWalk() {
        super("AutoWalk", Category.Movement);
        INSTANCE = this;
        setDescription("Automatically makes you walk");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), true);
    }
}
