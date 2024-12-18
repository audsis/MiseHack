package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.MessageEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.impl.features.modules.misc.Warner;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Unique
    private boolean ignoreChatMessage;

    @Shadow
    public abstract void sendChatMessage(String content);

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {

        if (ignoreChatMessage) return;
        ci.cancel();

        MessageEvent event = new MessageEvent(message);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        ignoreChatMessage = true;
        sendChatMessage(event.message);
        ignoreChatMessage = false;
    }

    @Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onEntityStatusHook(EntityStatusS2CPacket packet, CallbackInfo ci, Entity entity, int i) {
        Warner.INSTANCE.onPop(entity);
    }
}
