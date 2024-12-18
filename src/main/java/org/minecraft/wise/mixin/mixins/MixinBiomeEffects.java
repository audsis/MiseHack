package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.CustomSky;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeEffects.class)
public class MixinBiomeEffects {

    @Inject(method = "getSkyColor", at = @At(value = "HEAD"), cancellable = true)
    private void getSkyColor(CallbackInfoReturnable<Integer> cir) {
        if (CustomSky.INSTANCE.isEnabled() && CustomSky.INSTANCE.skyColor.getValue()) {
            cir.cancel();
            cir.setReturnValue(CustomSky.INSTANCE.sky.getValue().getRGB());
        }
    }

}
