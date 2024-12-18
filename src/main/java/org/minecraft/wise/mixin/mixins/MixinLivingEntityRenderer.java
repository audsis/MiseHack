package org.minecraft.wise.mixin.mixins;

import com.google.common.collect.Lists;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.minecraft.wise.impl.features.modules.render.Chams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected M model;

    @Shadow
    protected final List<FeatureRenderer<LivingEntity, EntityModel<LivingEntity>>> features = Lists.newArrayList();



    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void renderAsf(T livingEntity,
                           float f,
                           float g,
                           MatrixStack matrixStack,
                           VertexConsumerProvider vertexConsumerProvider,
                           int i,
                           CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity && Chams.INSTANCE.isEnabled() && Chams.INSTANCE.players.getValue()) {
            if (!Chams.INSTANCE.onlyInvisible.getValue())
                ci.cancel();

            if (Chams.INSTANCE.fill.getValue())
                Chams.INSTANCE.renderPlayer(livingEntity, g, matrixStack, vertexConsumerProvider, i, (EntityModel<LivingEntity>) model, features);
        }
    }
}
