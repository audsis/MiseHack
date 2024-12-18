package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.ParticleEditor;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public class MixinFireworkParticle extends NoRenderParticle {

    protected MixinFireworkParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(ClientWorld world,
                      double x,
                      double y,
                      double z,
                      double velocityX,
                      double velocityY,
                      double velocityZ,
                      ParticleManager particleManager,
                      List fireworkExplosions,
                      CallbackInfo ci) {
        if (ParticleEditor.INSTANCE.isEnabled() && ParticleEditor.INSTANCE.rockets.getValue()) {
            scale(ParticleEditor.INSTANCE.scale.getValue().floatValue());

            int rando = random.nextInt(2);

            switch (rando) {
                case 0:
                    setColor(ParticleEditor.INSTANCE.rocketColor1.getValue().getRed(),
                            ParticleEditor.INSTANCE.rocketColor1.getValue().getGreen(),
                            ParticleEditor.INSTANCE.rocketColor1.getValue().getBlue());
                    break;
                case 1:
                    setColor(ParticleEditor.INSTANCE.rocketColor2.getValue().getRed(),
                            ParticleEditor.INSTANCE.rocketColor2.getValue().getGreen(),
                            ParticleEditor.INSTANCE.rocketColor2.getValue().getBlue());
                    break;
            }
        }
    }
}
