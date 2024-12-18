package org.minecraft.wise.mixin.mixins.access;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={WorldRenderer.class})
public interface IWorldRenderer {
    @Accessor(value="frustum")
    Frustum getFrustum();

    @Accessor
    void setEntityOutlinesFramebuffer(Framebuffer var1);
}

