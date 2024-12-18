package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Undead extends Module {

    private boolean bypass = false;

    public Undead() {
        super("Undead", Category.Player);
    }


    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (mc.player.getHealth() == 0.0f) {
            mc.player.setHealth(20.0f);
            bypass = true;
            mc.setScreen(null);
            mc.player.setPosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        bypass = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            mc.player.requestRespawn();
        }
        bypass = false;
    }


    @Subscribe
    public void onPacket(PacketEvent event) {
        if (bypass && event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.cancel();
        }
    }
}
