package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.EntityAddEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.impl.features.modules.render.CustomSky;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(method = "addEntity", at = @At(value = "HEAD"))
    private void hookAddEntity(Entity entity, CallbackInfo ci) {
        EntityAddEvent event = new EntityAddEvent(entity);
        Bus.EVENT_BUS.post(event);
    }

    @Inject(method = "getSkyColor", at = @At(value = "HEAD"), cancellable = true)
    private void getSkyColor(CallbackInfoReturnable<Vec3d> cir) {
        if (CustomSky.INSTANCE.isEnabled() && CustomSky.INSTANCE.skyColor.getValue()) {
            Color color = CustomSky.INSTANCE.sky.getValue();
            cir.cancel();
            cir.setReturnValue(new Vec3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0));
        }
    }

    @Inject(method = "getCloudsColor", at = @At(value = "HEAD"), cancellable = true)
    private void getCloudsColor(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (CustomSky.INSTANCE.isEnabled() && CustomSky.INSTANCE.cloudColor.getValue()) {
            Color color = CustomSky.INSTANCE.clouds.getValue();
            cir.cancel();
            cir.setReturnValue(new Vec3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0));
        }
    }
}
