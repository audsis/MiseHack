package org.minecraft.wise.mixin.mixins;

import io.netty.buffer.Unpooled;
import org.minecraft.wise.api.utils.entity.InteractType;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.api.ducks.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInteractEntityC2SPacket.class)
public abstract class MixinPlayerInteractEntityC2SPacket implements IPlayerInteractEntityC2SPacket, IMinecraft {

    @Shadow
    @Final
    private int entityId;

    @Shadow
    protected abstract void write(PacketByteBuf buf);

    @Override
    public Entity getEntity() {
        return mc.world.getEntityById(entityId);
    }

    @Override
    public InteractType getType() {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        write(packetBuf);
        packetBuf.readVarInt();
        return packetBuf.readEnumConstant(InteractType.class);
    }
}