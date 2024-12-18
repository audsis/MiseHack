package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.ParticleEditor;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TotemParticle.class)
public class MixinTotemParticle extends AnimatedParticle {

    protected MixinTotemParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z, spriteProvider, upwardsAcceleration);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(ClientWorld world,
                      double x,
                      double y,
                      double z,
                      double velocityX,
                      double velocityY,
                      double velocityZ,
                      SpriteProvider spriteProvider,
                      CallbackInfo ci) {
        if (ParticleEditor.INSTANCE.isEnabled() && ParticleEditor.INSTANCE.totems.getValue()) {
            scale(ParticleEditor.INSTANCE.scale.getValue().floatValue());

            int rando = random.nextInt(2);

            switch (rando) {
                case 0:
                    setColor(ParticleEditor.INSTANCE.totemColor1.getValue().getRGB());
                    break;
                case 1:
                    setColor(ParticleEditor.INSTANCE.totemColor2.getValue().getRGB());
                    break;
            }
        }
    }
}
