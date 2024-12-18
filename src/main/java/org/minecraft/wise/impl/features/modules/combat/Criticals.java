package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.entity.InteractType;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.ducks.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Criticals extends Module {
    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Packet").withModes("Packet", "Strict", "Jump", "LowHop", "Grim").register(this);

    public Criticals() {
        super("Criticals", Category.Combat);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if ((event.getPacket() instanceof IPlayerInteractEntityC2SPacket packet && packet.getType() == InteractType.ATTACK && mc.player.isOnGround())) {
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();

            if (packet.getEntity() instanceof EndCrystalEntity) return;

            switch (mode.getValue()) {
                case "Grim" -> {
                    if (!mc.player.isOnGround())
                        send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y - 0.000001, z, false));
                }
                case "Packet" -> {
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.05, z, false));
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.03, z, false));
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                }
                case "Strict" -> {
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625d, z, false));
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                    send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.1e-5d, z, false));
                }
                case "Jump" -> mc.player.jump();
                case "LowHop" -> {
                    mc.player.setVelocity(new Vec3d(0, 0.3425, 0));
                    mc.player.fallDistance = 0.1f;
                    mc.player.setOnGround(false);
                }
            }
        }
    }
}
