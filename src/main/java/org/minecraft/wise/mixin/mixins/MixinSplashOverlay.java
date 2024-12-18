package org.minecraft.wise.mixin.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay implements IMinecraft {

    @Shadow
    static final Identifier LOGO = Identifier.ofVanilla("textures/gui/title/mojangstudios.png");

    @Final
    @Shadow
    private boolean reloading;

    @Shadow
    private long reloadStartTime = -1L;

    @Shadow
    private long reloadCompleteTime = -1L;

    @Final
    @Shadow
    private ResourceReload reload;

    @Shadow
    private float progress;

    @Final
    @Shadow
    private Consumer<Optional<Throwable>> exceptionHandler;

    @Unique
    private static final int wisehack = ColorHelper.Argb.getArgb(255, 188, 73, 255);

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    public DrawContext modifySplashRendering(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = context.getScaledWindowWidth();
        int j = context.getScaledWindowHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }

        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
        float h;
        int k;
        if (f >= 1.0F) {
            if (mc.currentScreen != null) {
                mc.currentScreen.render(context, 0, 0, delta);
            }

            k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, i, j, ColorUtil.newAlpha(wisehack, k).getRGB());
            h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (mc.currentScreen != null && g < 1.0F) {
                mc.currentScreen.render(context, mouseX, mouseY, delta);
            }

            k = MathHelper.ceil(MathHelper.clamp(g, 0.15, 1.0) * 255.0);
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, i, j, ColorUtil.newAlpha(wisehack, k).getRGB());
            h = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            k = wisehack;
            float m = (float)(k >> 16 & 255) / 255.0F;
            float n = (float)(k >> 8 & 255) / 255.0F;
            float o = (float)(k & 255) / 255.0F;
            GlStateManager._clearColor(m, n, o, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0F;
        }

        k = (int)((double)context.getScaledWindowWidth() * 0.5);
        int p = (int)((double)context.getScaledWindowHeight() * 0.5);
        double d = Math.min((double)context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
        int q = (int)(d * 0.5);
        double e = d * 4.0;
        int r = (int)(e * 0.5);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        context.setShaderColor(1.0F, 1.0F, 1.0F, h);
        context.drawTexture(LOGO, k - r, p - q, r, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
        context.drawTexture(LOGO, k, p - q, r, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        int s = (int)((double)context.getScaledWindowHeight() * 0.8325);
        float t = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            this.renderProgressBar(context, i / 2 - r, s - 5, i / 2 + r, s + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            mc.setOverlay(null);
        }

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            } catch (Throwable var23) {
                Throwable throwable = var23;
                this.exceptionHandler.accept(Optional.of(throwable));
            }

            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (mc.currentScreen != null) {
                mc.currentScreen.init(mc, context.getScaledWindowWidth(), context.getScaledWindowHeight());
            }
        }

        return context;
    }

    @Shadow
    private void renderProgressBar(DrawContext drawContext, int minX, int minY, int maxX, int maxY, float opacity) {
        int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
        int j = Math.round(opacity * 255.0F);
        int k = ColorHelper.Argb.getArgb(j, 255, 255, 255);
        drawContext.fill(minX + 2, minY + 2, minX + i, maxY - 2, k);
        drawContext.fill(minX + 1, minY, maxX - 1, minY + 1, k);
        drawContext.fill(minX + 1, maxY, maxX - 1, maxY - 1, k);
        drawContext.fill(minX, minY, minX + 1, maxY, k);
        drawContext.fill(maxX, minY, maxX - 1, maxY, k);
    }
}
