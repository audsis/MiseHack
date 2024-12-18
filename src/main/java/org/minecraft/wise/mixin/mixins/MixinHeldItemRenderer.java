package org.minecraft.wise.mixin.mixins;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.minecraft.wise.api.utils.render.shader.post.EntityShader;
import org.minecraft.wise.api.utils.render.shader.post.PostProcessShaders;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.render.Shaders;
import org.minecraft.wise.impl.features.modules.render.ViewmodelChanger;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer implements IMinecraft {

//    @Shadow
//    public abstract void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light);

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void onRenderItem(AbstractClientPlayerEntity player,
                              float tickDelta,
                              float pitch,
                              Hand hand,
                              float swingProgress,
                              ItemStack item,
                              float equipProgress,
                              MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers,
                              int light,
                              CallbackInfo ci) {

        if (ViewmodelChanger.INSTANCE.isEnabled()) {
            ViewmodelChanger.INSTANCE.doModel(matrices, hand);
        }
    }

//    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "HEAD"), cancellable = true)
//    private void onRenderItemHook(float tickDelta,
//                                  MatrixStack matrices,
//                                  VertexConsumerProvider.Immediate vertexConsumers,
//                                  ClientPlayerEntity player,
//                                  int light,
//                                  CallbackInfo ci) {
//        matrices.push();
//
//        if (Shaders.INSTANCE.isEnabled() && !PostProcessShaders.isCustom(vertexConsumers) && Shaders.INSTANCE.hands.getValue()) {
//            ci.cancel();
//            PostProcessShaders.beginRender();
//            PostProcessShaders.rendering = true;
//
//            renderItem(tickDelta, matrices, vertexConsumers, player, light);
//            vertexConsumers.draw();
//
//            PostProcessShaders.rendering = false;
//            PostProcessShaders.endRender();
//        }
//
//        matrices.pop();
//    }
}
