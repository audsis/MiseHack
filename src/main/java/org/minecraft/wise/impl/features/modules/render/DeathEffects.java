package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.tick.Tick;
import org.minecraft.wise.api.event.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.sound.SoundEvents;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.module.Module;

public class DeathEffects extends Module {
    public static DeathEffects INSTANCE;

    public DeathEffects() {
        super("DeathEffects", Feature.Category.Render);
        INSTANCE = this;
        setDescription("Displays lightning when you've killed the opponent");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.deathTime > 0 || player.getHealth() <= 0) {
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();

                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, player.getWorld());
                lightning.setPos(x, y, z);

                player.getWorld().spawnEntity(lightning);

                player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 1.0f);
            }
        }
    }
}
