package org.minecraft.wise.mixin.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.render.FovModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity implements IMinecraft {

    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> info) {
        if (mc.player != null && FovModifier.INSTANCE.isEnabled()) {
            float f = 1.0F;

            if (FovModifier.INSTANCE.staticaf.getValue())
                info.setReturnValue(1.0f);

            if (getAbilities().flying)
                f *= FovModifier.INSTANCE.flying.getValue().floatValue();

            f *= ((float) getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / getAbilities().getWalkSpeed() + FovModifier.INSTANCE.sprinting.getValue().floatValue()) / 2.0F;
            if (getAbilities().getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
                f = 1.0F;


            StatusEffectInstance speed = mc.player.getStatusEffect(StatusEffects.SPEED);
            StatusEffectInstance slowness = mc.player.getStatusEffect(StatusEffects.SLOWNESS);

            if (speed != null)
                f *= FovModifier.INSTANCE.swiftness.getValue().floatValue();

            if (slowness != null)
                f *= FovModifier.INSTANCE.slowness.getValue().floatValue();


            ItemStack itemStack = getActiveItem();

            if (isUsingItem()) {
                if (itemStack.isOf(Items.BOW)) {
                    int i = getItemUseTime();
                    float g = (float) i / 20.0F;

                    if (g > 1.0F) {
                        g = 1.0F;
                    } else {
                        g *= g;
                    }

                    f *= FovModifier.INSTANCE.aim.getValue().floatValue() - g * 0.15F;
                } else if (mc.options.getPerspective().isFirstPerson() && isUsingSpyglass())
                    info.setReturnValue(FovModifier.INSTANCE.spyglass.getValue().floatValue());
            }

            info.setReturnValue(MathHelper.lerp(mc.options.getFovEffectScale().getValue().floatValue(), 1.0F, f));
        }
    }
}
