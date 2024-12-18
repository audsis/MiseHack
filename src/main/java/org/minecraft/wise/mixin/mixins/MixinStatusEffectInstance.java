package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.NoRender;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class MixinStatusEffectInstance {

    @Inject(at={@At(value="HEAD")}, method={"shouldShowIcon"}, cancellable=true)
    private void init(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.potionHud.getValue()) {
            cir.setReturnValue(false);
        }
    }
}
