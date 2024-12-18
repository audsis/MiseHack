package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.BlockHighlight;
import org.minecraft.wise.impl.features.modules.render.Shaders;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.Framebuffer;
import org.minecraft.wise.api.utils.render.shader.post.EntityShader;
import org.minecraft.wise.api.utils.render.shader.post.PostProcessShaders;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IMinecraft {

    @Shadow
    private Framebuffer entityOutlinesFramebuffer;

    @Shadow
    protected abstract void renderEntity(Entity p0, double p1, double p2, double p3, float p4, MatrixStack p5, VertexConsumerProvider p6);

//    @Inject(method = "render", at = @At("TAIL"))
//    private void renderGAAANG(RenderTickCounter tickCounter,
//                        boolean renderBlockOutline,
//                        Camera camera,
//                        GameRenderer gameRenderer,
//                        LightmapTextureManager lightmapTextureManager,
//                        Matrix4f matrix4f,
//                        Matrix4f matrix4f2,
//                        CallbackInfo ci,
//                        @Local MatrixStack stack) {
//
//        mc.getProfiler().push("wisehack-rendering-3d");
//
//        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
//        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(mc.gameRenderer.getCamera().getYaw() + 180f));
//
//        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
//        Render3dEvent event = new Render3dEvent(stack, tickCounter.getTickDelta(true));
//        Bus.EVENT_BUS.post(event);
//
//        mc.getProfiler().pop();
//
//    }

    @Inject(method={"onResized"}, at={@At(value="HEAD")})
    private void onResized(int width, int height, CallbackInfo info) {
        PostProcessShaders.onResized(width, height);
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void onRenderHead(RenderTickCounter tickCounter,
                              boolean renderBlockOutline,
                              Camera camera,
                              GameRenderer gameRenderer,
                              LightmapTextureManager lightmapTextureManager,
                              Matrix4f matrix4f,
                              Matrix4f matrix4f2,
                              CallbackInfo ci) {
        PostProcessShaders.beginRender();
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V")})
    private void onRender(RenderTickCounter tickCounter,
                          boolean renderBlockOutline,
                          Camera camera,
                          GameRenderer gameRenderer,
                          LightmapTextureManager lightmapTextureManager,
                          Matrix4f matrix4f,
                          Matrix4f matrix4f2,
                          CallbackInfo ci) {
        PostProcessShaders.endRender();
    }

    @Inject(method={"renderEntity"}, at={@At(value="HEAD")})
    private void renderEntity(Entity entity,
                              double cameraX,
                              double cameraY,
                              double cameraZ,
                              float tickDelta,
                              MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers,
                              CallbackInfo ci) {
        Color real = Shaders.INSTANCE.color.getValue();

        draw(entity, cameraX, cameraY, cameraZ, tickDelta, vertexConsumers, matrices, PostProcessShaders.ENTITY_OUTLINE, real);
    }

    @Unique
    private void draw(Entity entity,
                      double cameraX,
                      double cameraY,
                      double cameraZ,
                      float tickDelta,
                      VertexConsumerProvider vertexConsumers,
                      MatrixStack matrices,
                      EntityShader shader,
                      Color color) {
        if (shader.shouldDraw(entity) && !PostProcessShaders.isCustom(vertexConsumers) && color != null) {
            Framebuffer prevBuffer = entityOutlinesFramebuffer;
            entityOutlinesFramebuffer = shader.framebuffer;
            PostProcessShaders.rendering = true;
            shader.vertexConsumerProvider.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, shader.vertexConsumerProvider);

            PostProcessShaders.rendering = false;
            entityOutlinesFramebuffer = prevBuffer;
        }
    }

    @Inject(method = { "drawBlockOutline" }, at = { @At("HEAD") }, cancellable = true)
    private void highlightedBlockHook(MatrixStack matrixStack,
                                      VertexConsumer vertexConsumer,
                                      Entity entity,
                                      double d,
                                      double e,
                                      double f,
                                      BlockPos blockPos,
                                      BlockState blockState,
                                      CallbackInfo info) {
        if (BlockHighlight.INSTANCE.isEnabled()) {
            info.cancel();
        }
    }
}
