package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.CustomSky;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(DimensionEffects.class)
public class MixinDimensionEffects {

    @Inject(method = "getFogColorOverride", at = @At(value = "HEAD"), cancellable = true)
    private void getFogColorOverride(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> cir) {
        if (CustomSky.INSTANCE.isEnabled() && CustomSky.INSTANCE.fogColor.getValue()) {
            Color color = CustomSky.INSTANCE.fog.getValue();
            cir.cancel();
            cir.setReturnValue(new float[]{(float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, 1.0f});
        }
    }
}
