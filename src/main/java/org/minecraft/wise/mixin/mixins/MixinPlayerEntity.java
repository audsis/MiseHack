package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.JumpEvent;
import org.minecraft.wise.api.event.TravelEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements IMinecraft {

    @Inject(method = "travel", at = @At(value = "RETURN"), cancellable = true)
    private void hookTravelTail(Vec3d movementInput, CallbackInfo ci) {
        TravelEvent event = new TravelEvent(movementInput);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At(value = "HEAD"), cancellable = true)
    private void hookJump(CallbackInfo ci) {
        if ((Object) this != mc.player)
            return;

        JumpEvent event = new JumpEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
