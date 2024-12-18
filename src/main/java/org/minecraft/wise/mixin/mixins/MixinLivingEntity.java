package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.player.PlayerTweaks;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @ModifyConstant(method = {"tickMovement"}, constant = { @Constant(intValue = 10) })
    private int getJumpDelay(int hard) {
        return PlayerTweaks.INSTANCE.noJumpDelay.getValue() ? 0 : hard;
    }
}
