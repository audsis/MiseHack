package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.InputEvent;
import org.minecraft.wise.api.event.bus.Bus;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {


    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key != 0) {
            InputEvent event = new InputEvent(key, action);
            Bus.EVENT_BUS.post(event);
            if (event.isCancelled())
                ci.cancel();
        }
    }
}
