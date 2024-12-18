package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.movement.EntityControl;
import net.minecraft.entity.passive.PigEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public class MixinPigEntity {

    @Inject(method = "isSaddled", at = @At(value = "HEAD"), cancellable = true)
    private void isSaddled(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isEnabled()) {
            info.setReturnValue(true);
        }
    }
}
