package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.movement.EntityControl;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class MixinAbstractHorseEntity {

    @Inject(method = "isSaddled", at = @At(value = "HEAD"), cancellable = true)
    private void isSaddled(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isEnabled()) {
            info.setReturnValue(true);
        }
    }
}
