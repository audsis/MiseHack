package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.render.FullBright;
import org.minecraft.wise.impl.features.modules.render.NoRender;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager implements IMinecraft {

//    @Unique
//    private Identifier identifier;

    @Inject(method="getDarknessFactor(F)F", at=@At(value="HEAD"), cancellable=true)
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (NoRender.INSTANCE.deepDark.getValue()) {
            info.setReturnValue(0.0f);
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (FullBright.INSTANCE.isEnabled()) {
            args.set(2, -1);
        }

//        if (Ambience.INSTANCE.isEnabled()) {
//            args.set(2, Ambience.INSTANCE.color.getValue().getRGB());
//        }
    }
//
//    @Inject(method = "enable", at = @At("HEAD"), cancellable = true)
//    private void enableHead(CallbackInfo ci) {
//        if (Ambience.rendering) {
//            RenderSystem.setShaderTexture(2, identifier);
//            mc.getTextureManager().bindTexture(identifier);
//            RenderSystem.texParameter(3553, 10241, 9729);
//            RenderSystem.texParameter(3553, 10240, 9729);
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void initTail(GameRenderer renderer, MinecraftClient client, CallbackInfo ci) {
//        NativeImageBackedTexture texture = new NativeImageBackedTexture(16, 16, false);
//        NativeImage image = texture.getImage();
//        identifier = client.getTextureManager().registerDynamicTexture("customlightmap", texture);
//
//        for (int x = 0; x < 16; x++) {
//            for (int y = 0; y < 16; y++) {
//                image.setColor(x, y, -1);
//            }
//        }
//        texture.upload();
//    }
}
