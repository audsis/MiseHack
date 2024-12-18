package org.minecraft.wise.api.utils.render.shader.post;

import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.mixin.mixins.access.IWorldRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.WorldRenderer;

public abstract class EntityShader extends PostProcessShader implements IMinecraft {
    private Framebuffer prevBuffer;

    @Override
    protected void preDraw() {
        WorldRenderer worldRenderer = mc.worldRenderer;

        IWorldRenderer wra = (IWorldRenderer) worldRenderer;
        prevBuffer = worldRenderer.getEntityOutlinesFramebuffer();

        wra.setEntityOutlinesFramebuffer(framebuffer);
    }

    @Override
    protected void postDraw() {
        if (prevBuffer == null) {
            return;
        }

        WorldRenderer worldRenderer = mc.worldRenderer;

        IWorldRenderer wra = (IWorldRenderer) worldRenderer;
        wra.setEntityOutlinesFramebuffer(prevBuffer);

        prevBuffer = null;
    }

    public void endRender() {
        endRender(() -> vertexConsumerProvider.draw());
    }
}

