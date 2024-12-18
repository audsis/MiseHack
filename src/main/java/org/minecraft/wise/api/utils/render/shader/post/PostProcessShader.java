package org.minecraft.wise.api.utils.render.shader.post;

import org.minecraft.wise.api.utils.render.shader.GL;
import org.minecraft.wise.api.utils.render.shader.PostProcessRenderer;
import org.minecraft.wise.api.utils.render.shader.Shader;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class PostProcessShader implements IMinecraft {
    public OutlineVertexConsumerProvider vertexConsumerProvider;
    public Framebuffer framebuffer;
    protected Shader shader;

    public void init(String frag) {
        vertexConsumerProvider = new OutlineVertexConsumerProvider(mc.getBufferBuilders().getEntityVertexConsumers());
        framebuffer = new SimpleFramebuffer(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
        shader = new Shader("vertex.vert", "post/" + frag + ".frag");
    }

    protected abstract boolean shouldDraw();
    public abstract boolean shouldDraw(Entity entity);

    protected void preDraw() {}
    protected void postDraw() {}

    protected abstract void setUniforms();

    public void beginRender() {
        if (!shouldDraw()) return;

        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        mc.getFramebuffer().beginWrite(false);
    }

    public void endRender(Runnable draw) {
        if (!shouldDraw()) return;

        preDraw();
        draw.run();
        postDraw();

        mc.getFramebuffer().beginWrite(false);

        GL.bindTexture(framebuffer.getColorAttachment(), 0);

        shader.bind();

        shader.set("u_Size", mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
        shader.set("u_Texture", 0);
        shader.set("u_Time", glfwGetTime());
        setUniforms();

        PostProcessRenderer.render();
    }

    public void onResized(int width, int height) {
        if (framebuffer == null) return;
        framebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
    }
}