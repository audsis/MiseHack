package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.KeyboardEvent;
import org.minecraft.wise.api.event.bus.Bus;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/" + "client/input/KeyboardInput;sneaking:Z", shift = At.Shift.BEFORE), cancellable = true)
    private void hookTick$Post(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        KeyboardEvent event = new KeyboardEvent((Input) (Object) this);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
            ci.cancel();
    }
}
