package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.impl.features.modules.render.NoRender;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Inject(at=@At(value="HEAD"), method="addParticle(Lnet/minecraft/client/particle/Particle;)V", cancellable=true)
    public void addParticleHook(Particle particle, CallbackInfo e) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.explosions.getValue() && particle instanceof ExplosionLargeParticle)
            e.cancel();

        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.breakParticles.getValue() && particle instanceof BlockDustParticle)
            e.cancel();

        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.fireworks.getValue() && (particle instanceof FireworksSparkParticle.FireworkParticle || particle instanceof FireworksSparkParticle.Flash))
            e.cancel();

    }
}