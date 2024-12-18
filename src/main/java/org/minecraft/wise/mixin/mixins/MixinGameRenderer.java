package org.minecraft.wise.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.render.AspectRatio;
import org.minecraft.wise.impl.features.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDate;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IMinecraft {

    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;
    @Shadow
    private float viewDistance;

    @Unique
    private final LocalDate date = LocalDate.now();

    @Unique
    private static boolean isOctober31st(LocalDate date) {
        return date.getMonthValue() == 10 && date.getDayOfMonth() == 31;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack stack) {
        mc.getProfiler().push("wisehack-rendering-3d");

        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(mc.gameRenderer.getCamera().getYaw() + 180f));

        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        Render3dEvent event = new Render3dEvent(stack, tickCounter.getTickDelta(true));
        Bus.EVENT_BUS.post(event);

        mc.getProfiler().pop();
    }

    @Inject(method={"renderHand"}, at={@At(value="HEAD")})
    private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
        if (mc.player == null && mc.world == null)
            return;

        if (MixinGameRenderer.isOctober31st(date)) {
            RenderSystem.setShaderColor(100.0f, 1.0f, 1.0f, 255.0f);
        }
    }

    @Redirect(method="renderWorld", at=@At(value="INVOKE", target="Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float applyCameraTransformationsMathHelperLerpProxy(float delta, float first, float second) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.nausea.getValue())
            return 0.0f;

        return MathHelper.lerp(delta, first, second);
    }

    @Inject(method={"getBasicProjectionMatrix"}, at={@At(value="TAIL")}, cancellable=true)
    public void getBasicProjectionMatrixHook(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (AspectRatio.INSTANCE.isEnabled()) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.peek().getPositionMatrix().identity();

            if (zoom != 1.0f) {
                matrixStack.translate(zoomX, -zoomY, 0.0f);
                matrixStack.scale(zoom, zoom, 1.0f);
            }

            matrixStack.peek().getPositionMatrix().mul(new Matrix4f().setPerspective((float) (fov * 0.01745329238474369f), AspectRatio.INSTANCE.ratio.getValue().floatValue(), 0.05f, viewDistance * 4.0f));
            cir.setReturnValue(matrixStack.peek().getPositionMatrix());
        }
    }

//    @Inject(method = "render", at = @At("TAIL"))
//    private void renderTail(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
//        Ambience.rendering = false;
//    }
//
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V"))
//    private void renderInvoke$clear(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
//        Ambience.rendering = true;
//    }

    @Inject(method = "tiltViewWhenHurt", at = @At(value="HEAD"), cancellable = true)
    private void tiltViewWhenHurtHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.noHurtCam.getValue()) {
            ci.cancel();
        }
    }
}
