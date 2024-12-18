package org.minecraft.wise.api.utils.render.shader;

import org.minecraft.wise.api.wrapper.IMinecraft;

import static org.lwjgl.opengl.GL32C.*;

public class Framebuffer implements IMinecraft {
    private int id;
    public int texture;
    public double sizeMulti = 1;
    public int width, height;

    public Framebuffer(double sizeMulti) {
        this.sizeMulti = sizeMulti;
        init();
    }

    public Framebuffer() {
        init();
    }

    private void init() {
        id = GL.genFramebuffer();
        bind();

        texture = GL.genTexture();
        GL.bindTexture(texture);
        GL.defaultPixelStore();

        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        width = (int) (mc.getWindow().getFramebufferWidth() * sizeMulti);
        height = (int) (mc.getWindow().getFramebufferHeight() * sizeMulti);

        GL.textureImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
        GL.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        unbind();
    }

    public void bind() {
        GL.bindFramebuffer(id);
    }

    public void setViewport() {
        GL.viewport(0, 0, width, height);
    }

    public void unbind() {
        mc.getFramebuffer().beginWrite(false);
    }

    public void resize() {
        GL.deleteFramebuffer(id);
        GL.deleteTexture(texture);

        init();
    }
}