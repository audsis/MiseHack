package org.minecraft.wise.api.utils.render.shader.post;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;

public class PostProcessShaders {
    public static EntityShader CHAMS;
    public static EntityShader ENTITY_OUTLINE;
    public static PostProcessShader STORAGE_OUTLINE;
    public static MinecraftClient mc;
    public static boolean rendering;

    private PostProcessShaders() {
    }

    public static void init() {
        mc = MinecraftClient.getInstance();
        ENTITY_OUTLINE = new EntityOutlineShader();
    }

    public static void beginRender() {
        ENTITY_OUTLINE.beginRender();
    }

    public static void endRender() {
        ENTITY_OUTLINE.endRender();
    }

    public static void onResized(int width, int height) {
        if (mc == null) {
            return;
        }

        ENTITY_OUTLINE.onResized(width, height);
    }

    public static boolean isCustom(VertexConsumerProvider vcp) {
        return vcp == PostProcessShaders.ENTITY_OUTLINE.vertexConsumerProvider;
    }
}

