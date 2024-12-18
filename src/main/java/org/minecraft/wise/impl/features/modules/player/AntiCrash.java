package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class AntiCrash extends Module {

    public AntiCrash() {
        super("AntiCrash", Category.Player);
        setDescription("Prevents crashing from certain nuking plugins.");
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof ExplosionS2CPacket exp) {
            if (exp.getX() > 1.0E9 || exp.getY() > 1.0E9 || exp.getZ() > 1.0E9 || exp.getRadius() > 1.0E9) {
                event.cancel();
            }
        } else {
            if (event.getPacket() instanceof ParticleS2CPacket p) {
                if (p.getX() > 1.0E9 || p.getY() > 1.0E9 || p.getZ() > 1.0E9 || p.getSpeed() > 1.0E9 || p.getOffsetX() > 1.0E9 || p.getOffsetY() > 1.0E9 || p.getOffsetZ() > 1.0E9) {
                    event.cancel();
                }
            }
            else {
                if (event.getPacket() instanceof PlayerPositionLookS2CPacket pos) {
                    if (pos.getX() > 1.0E9 || pos.getY() > 1.0E9 || pos.getZ() > 1.0E9 || pos.getYaw() > 1.0E9 || pos.getPitch() > 1.0E9) {
                        event.cancel();
                    }
                }
            }
        }
    }
}
