package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.player.Blink;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {

    public final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Packet").withModes("Packet", "Sneak", "Blink").register(this);
    private boolean blinked = false;

    public NoFall() {
        super("NoFall", Category.Movement);
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mode.getValue().equals("Packet") && isFalling()) {
            send(new PlayerMoveC2SPacket.OnGroundOnly(true));
            mc.player.fallDistance = 0.0f;
        } else if (mode.getValue().equals("Blink")) {
            if (blinked && mc.player.isOnGround()) {
                Blink.INSTANCE.setEnabled(false);
                blinked = false;
            }

            if (isFalling()) {
                Blink.INSTANCE.setEnabled(true);
                blinked = true;
            }
        }
    }

    public boolean isFalling() {
        return !mc.player.isFallFlying() && mc.player.fallDistance > 2.0f;
    }
}
