package org.minecraft.wise.mixin.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import org.minecraft.wise.impl.features.modules.render.CustomSky;
import org.minecraft.wise.impl.features.modules.render.NoRender;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(method = "applyFog", at = @At(value = "TAIL"))
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.liquidVision.getValue() && (camera.getSubmersionType() == CameraSubmersionType.LAVA || camera.getSubmersionType() == CameraSubmersionType.WATER)) {
            RenderSystem.setShaderFogStart((viewDistance * 4.0f));
            RenderSystem.setShaderFogEnd((viewDistance * 4.25f));
        }
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN && NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.fog.getValue()) {
            RenderSystem.setShaderFogStart((viewDistance * 4.0f));
            RenderSystem.setShaderFogEnd((viewDistance * 4.25f));
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private static void hookRender(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        if (CustomSky.INSTANCE.isEnabled() && CustomSky.INSTANCE.fogColor.getValue()) {
            ci.cancel();
            RenderSystem.clearColor(CustomSky.INSTANCE.fog.getValue().getRed() / 255.0f,
                    CustomSky.INSTANCE.fog.getValue().getGreen() / 255.0f,
                    CustomSky.INSTANCE.fog.getValue().getBlue() / 255.0f,
                    0.0f);
        }
    }


    @Inject(method = "getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;", at = @At(value = "HEAD"), cancellable = true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> cir) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.blindness.getValue()) {
            cir.setReturnValue(null);
        }
    }
}

