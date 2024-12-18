package org.minecraft.wise.api.utils.render.antialiasing;

import java.util.ArrayList;
import java.util.HashMap;

import org.minecraft.wise.impl.features.modules.client.Manager;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL30;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gl.Framebuffer;

public class AntialiasingFramebuffer extends Framebuffer
{
    public static final int MAX_SAMPLES;
    private static final Map<Integer, AntialiasingFramebuffer> INSTANCES;
    private static final List<AntialiasingFramebuffer> ACTIVE_INSTANCES;
    private final int samples;
    private int rboColor;
    private int rboDepth;
    private boolean inUse;

    private AntialiasingFramebuffer(int samples) {
        super(true);
        if (samples < 2 || samples > MAX_SAMPLES)
            throw new IllegalArgumentException(String.format("The number of samples should be >= %s and <= %s.", 2, MAX_SAMPLES));

        if ((samples & samples - 1) != 0x0)
            throw new IllegalArgumentException("The number of samples must be a power of two.");

        this.samples = samples;
        setClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    public static boolean framebufferInUse() {
        return !ACTIVE_INSTANCES.isEmpty();
    }

    public static AntialiasingFramebuffer getInstance() {
        return INSTANCES.computeIfAbsent(Manager.INSTANCE.getMultiplier(), x -> new AntialiasingFramebuffer(Manager.INSTANCE.getMultiplier()));
    }

    public static void start(AntialiasingFramebuffer msaaBuffer, Framebuffer mainBuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        msaaBuffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, true);

        GlStateManager._glBindFramebuffer(36008, mainBuffer.fbo);
        GlStateManager._glBindFramebuffer(36009, msaaBuffer.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 16384, 9729);

        msaaBuffer.beginWrite(true);
    }

    public static void end(AntialiasingFramebuffer msaaBuffer, Framebuffer mainBuffer) {
        msaaBuffer.endWrite();

        GlStateManager._glBindFramebuffer(36008, msaaBuffer.fbo);
        GlStateManager._glBindFramebuffer(36009, mainBuffer.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 16384, 9729);

        msaaBuffer.clear(true);
        mainBuffer.beginWrite(false);
    }

    public void resize(int width, int height, boolean getError) {
        if (textureWidth != width || textureHeight != height)
            super.resize(width, height, getError);
    }

    public void initFbo(int width, int height, boolean getError) {
        RenderSystem.assertOnRenderThreadOrInit();
        int maxSize = RenderSystem.maxSupportedTextureSize();

        if (width <= 0 || width > maxSize || height <= 0 || height > maxSize)
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + maxSize);

        viewportWidth = width;
        viewportHeight = height;
        textureWidth = width;
        textureHeight = height;

        GlStateManager._glBindFramebuffer(36160, fbo = GlStateManager.glGenFramebuffers());
        GlStateManager._glBindRenderbuffer(36161, rboColor = GlStateManager.glGenRenderbuffers());

        GL30.glRenderbufferStorageMultisample(36161, samples, 32856, width, height);

        GlStateManager._glBindRenderbuffer(36161, 0);
        GlStateManager._glBindRenderbuffer(36161, rboDepth = GlStateManager.glGenRenderbuffers());

        GL30.glRenderbufferStorageMultisample(36161, samples, 6402, width, height);
        GlStateManager._glBindRenderbuffer(36161, 0);

        GL30.glFramebufferRenderbuffer(36160, 36064, 36161, rboColor);
        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, rboDepth);

        colorAttachment = MinecraftClient.getInstance().getFramebuffer().getColorAttachment();
        depthAttachment = MinecraftClient.getInstance().getFramebuffer().getDepthAttachment();

        checkFramebufferStatus();
        clear(getError);

        endRead();
    }

    public void delete() {
        RenderSystem.assertOnRenderThreadOrInit();
        endRead();
        endWrite();

        if (fbo > -1) {
            GlStateManager._glBindFramebuffer(36160, 0);
            GlStateManager._glDeleteFramebuffers(fbo);
            fbo = -1;
        }

        if (rboColor > -1) {
            GlStateManager._glDeleteRenderbuffers(rboColor);
            rboColor = -1;
        }

        if (rboDepth > -1) {
            GlStateManager._glDeleteRenderbuffers(rboDepth);
            rboDepth = -1;
        }

        colorAttachment = -1;
        depthAttachment = -1;
        textureWidth = -1;
        textureHeight = -1;
    }

    public void beginWrite(boolean setViewport) {
        super.beginWrite(setViewport);

        if (!inUse) {
            ACTIVE_INSTANCES.add(this);
            inUse = true;
        }
    }

    public void endWrite() {
        super.endWrite();

        if (inUse) {
            inUse = false;
            ACTIVE_INSTANCES.remove(this);
        }
    }

    static {
        MAX_SAMPLES = GL30.glGetInteger(36183);
        INSTANCES = new HashMap<>();
        ACTIVE_INSTANCES = new ArrayList<>();
    }
}
 
