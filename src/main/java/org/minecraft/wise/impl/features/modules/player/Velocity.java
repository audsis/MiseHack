package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.mixin.mixins.access.IExplosionS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;

import java.util.Objects;

public class Velocity extends Module {
    public static Velocity INSTANCE;

    public final Value<String> mode = new ValueBuilder<String>().withDescriptor("Type").withValue("Vanilla").withModes("Vanilla", "Grim", "2b2t").register(this);
    public final Value<Boolean> knockback = new ValueBuilder<Boolean>().withDescriptor("Knockback").withValue(true).register(this);
    public final Value<Boolean> blocks = new ValueBuilder<Boolean>().withDescriptor("Blocks").withValue(true).register(this);
    public final Value<Boolean> players = new ValueBuilder<Boolean>().withDescriptor("Players").withValue(true).register(this);
    public final Value<Boolean> fishingHook = new ValueBuilder<Boolean>().withDescriptor("FishingHook").withValue(true).register(this);

    int timeout;
    boolean dispatchVelo;

    public Velocity() {
        super("Velocity", Category.Player);
        timeout = 0;
        dispatchVelo = false;
        INSTANCE = this;
        setDescription("Cancels knockback from entities or crystals.");
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        final String currentValue = mode.getValue();
        switch (currentValue) {
            case "Vanilla": {
                if (!knockback.getValue()) {
                    break;
                }
                if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) event.getPacket()).getEntityId() == mc.player.getId()) {
                    event.cancel();
                }
                if (event.getPacket() instanceof ExplosionS2CPacket) {
                    event.cancel();
                }
                break;
            }
            case "Grim": {
                if (!knockback.getValue()) {
                    break;
                }
                if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
                    timeout = 25;
                    break;
                }
                --timeout;
                if (timeout >= 0) {
                    return;
                }
                if ((event.getPacket() instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) event.getPacket()).getEntityId() == mc.player.getId()) ||
                        event.getPacket() instanceof ExplosionS2CPacket) {
                    event.cancel();
                    dispatchVelo = true;
                }
                break;
            }
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof EntityStatusS2CPacket pac) {
            if (pac.getStatus() == 31) {
                Entity entity = pac.getEntity(mc.world);
                if (entity instanceof FishingBobberEntity hook) {
                    if (fishingHook.getValue() && Objects.equals(hook.getHookedEntity(), mc.player)) {
                        event.cancel();
                    }
                }
            }
        }
        if (mode.getValue().equals("2b2t")) {
            if (NullUtils.nullCheck() && mc.player.isFallFlying())
                return;

            if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
                event.cancel();
            }

            if (event.getPacket() instanceof ExplosionS2CPacket explosionS2CPacket) {
                ((IExplosionS2CPacket) explosionS2CPacket).setPlayerVelocityX(0);
                ((IExplosionS2CPacket) explosionS2CPacket).setPlayerVelocityY(0);
                ((IExplosionS2CPacket) explosionS2CPacket).setPlayerVelocityZ(0);
            }
        }
    }

    @Override
    public String getHudInfo() {
        return mode.getValue().equals("Vanilla") ? "Cancel" : "Grim";
    }
}
