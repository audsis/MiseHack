package org.minecraft.wise.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.joml.Vector3f;

public class WorldRenderer implements IMinecraft {
    public static final BufferAllocator buffer = new BufferAllocator(2048);
    private static final Vector3f[] shaderLight;

    static {
        try {
            shaderLight = (Vector3f[]) FieldUtils.getField(RenderSystem.class, "shaderLightDirections", true).get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void drawText(String text, double x, double y, double z, double scale, int color) {
        drawText(text, x, y, z, 0.0, 0.0, scale, color);
    }

    public static void drawText(String text, double x, double y, double z, double offX, double offY, double scale, int color) {
        MatrixStack matrices = matrixFrom(x, y, z);
        Camera camera = mc.gameRenderer.getCamera();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.translate(offX, offY, 0.0);
        matrices.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1.0f);

        int halfWidth = mc.textRenderer.getWidth(text) / 2;
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);

        matrices.push();
        matrices.translate(1.0f, 1.0f, 0.0f);

        FontManager.drawText(matrices, text, -halfWidth, 0, color);

        immediate.draw();
        matrices.pop();
        immediate.draw();

        RenderSystem.disableBlend();
    }

    public static void drawGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        if (item.isEmpty())
            return;

        MatrixStack matrices = matrixFrom(x, y, z);
        matrices.push();

        Camera camera = mc.gameRenderer.getCamera();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        matrices.translate(offX, offY, 0.0);
        matrices.scale((float) scale, (float) scale, 0.001f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

        mc.getBufferBuilders().getEntityVertexConsumers().draw();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vector3f[] currentLight = shaderLight.clone();
        DiffuseLighting.enableGuiDepthLighting();
        DiffuseLighting.method_56819(RotationAxis.POSITIVE_X.rotationDegrees(-1.0f));

        mc.getBufferBuilders().getEntityVertexConsumers().draw();
        mc.getItemRenderer().renderItem(item, ModelTransformationMode.GUI, 15728880, OverlayTexture.DEFAULT_UV, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), mc.world, 0);

        RenderSystem.setShaderLights(currentLight[0], currentLight[1]);
        RenderSystem.disableBlend();
        matrices.pop();
    }

    public static void drawGuiItem(MatrixStack matrices, ItemStack item) {
        if (item.isEmpty())
            return;

        matrices.push();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f[] currentLight = shaderLight.clone();

        Camera camera = mc.gameRenderer.getCamera();

        DiffuseLighting.enableGuiDepthLighting();
        DiffuseLighting.method_56819(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        mc.getItemRenderer().renderItem(item, ModelTransformationMode.GUI, -1, OverlayTexture.DEFAULT_UV, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), mc.world, 0);

        RenderSystem.setShaderLights(currentLight[0], currentLight[1]);
        RenderSystem.disableBlend();

        matrices.pop();
    }

    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = mc.gameRenderer.getCamera();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
    }
}
