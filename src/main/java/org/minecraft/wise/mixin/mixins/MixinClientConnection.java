package org.minecraft.wise.mixin.mixins;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.impl.features.modules.misc.FastLatency;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    private Channel channel;

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        try {
            final PacketEvent event = new PacketEvent(packet, PacketEvent.Time.Receive);
            Bus.EVENT_BUS.post(event);

            if (event.isCancelled())
                info.cancel();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo info) {
        final PacketEvent event = new PacketEvent(packet, PacketEvent.Time.Send);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (channel.isOpen() && packet != null) {
            try {
                if (packet instanceof CommandSuggestionsS2CPacket commandSuggestionsS2CPacket && commandSuggestionsS2CPacket.id() == 1337) {
                    FastLatency.INSTANCE.ping = (int) (System.currentTimeMillis() - FastLatency.INSTANCE.responseTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
