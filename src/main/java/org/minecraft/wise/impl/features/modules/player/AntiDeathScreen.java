package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.client.gui.screen.DeathScreen;

public class AntiDeathScreen extends Module {

    public AntiDeathScreen() {
        super("AntiDeathScreen", Category.Player);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck()) return;

        if (mc.currentScreen instanceof DeathScreen && mc.player.getHealth() > 0.0F) {
            mc.setScreen(null);
        }
    }
}
