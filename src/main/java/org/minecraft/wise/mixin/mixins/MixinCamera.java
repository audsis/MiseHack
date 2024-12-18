package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.ViewClip;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(float f, CallbackInfoReturnable<Float> cir) {
        if (ViewClip.INSTANCE.isEnabled()) {
            cir.setReturnValue(ViewClip.INSTANCE.distance.getValue().floatValue());
        }
    }
}