package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.MouseEvent;
import org.minecraft.wise.api.event.bus.Bus;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MouseEvent event = new MouseEvent(button, action);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
            ci.cancel();
    }
}
