package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class AntiHunger extends Module {

    public AntiHunger() {
        super("AntiHunger", Category.Player);
    }

    @Subscribe
    public void onPacketSend(PacketEvent event) {
        if (event.getTime() == PacketEvent.Time.Send) {
            if (NullUtils.nullCheck())
                return;

            if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
                if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING || packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                    event.cancel();
                }
            }
        }
    }
}