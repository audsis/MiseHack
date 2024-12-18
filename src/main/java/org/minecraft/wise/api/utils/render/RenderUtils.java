package org.minecraft.wise.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.render.antialiasing.AntialiasingFramebuffer;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.Manager;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.minecraft.wise.api.utils.render.WorldRenderer.matrixFrom;

public class RenderUtils implements IMinecraft {
    public static boolean rendering3D = true;
    public static final BufferAllocator buffer = new BufferAllocator(2048);

    public static Vec3d getRenderPosition(Entity entity, float tickDelta) {
        return new Vec3d(entity.getX() - MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()), entity.getY() - MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()), entity.getZ() - MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        setup();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix, x, y + height, 0).color(color);
        bufferBuilder.vertex(matrix, x + width, y + height, 0).color(color);
        bufferBuilder.vertex(matrix, x + width, y, 0).color(color);
        bufferBuilder.vertex(matrix, x, y, 0).color(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        end();

    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color topLeft, Color bottomLeft, Color bottomRight, Color topRight) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        setup();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix, x, y, 0.0F).color(topLeft.getRed() / 255.0F, topLeft.getGreen() / 255.0F, topLeft.getBlue() / 255.0F, topLeft.getAlpha() / 255.0F);
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(bottomLeft.getRed() / 255.0F, bottomLeft.getGreen() / 255.0F, bottomLeft.getBlue() / 255.0F, bottomLeft.getAlpha() / 255.0F);
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(bottomRight.getRed() / 255.0F, bottomRight.getGreen() / 255.0F, bottomRight.getBlue() / 255.0F, bottomRight.getAlpha() / 255.0F);
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(topRight.getRed() / 255.0F, topRight.getGreen() / 255.0F, topRight.getBlue() / 255.0F, topRight.getAlpha() / 255.0F);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        end();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int topLeft, int bottomLeft, int bottomRight, int topRight) {
        Color topLeftColor = new Color(topLeft, true);
        Color bottomLeftColor = new Color(bottomLeft, true);
        Color bottomRightColor = new Color(bottomRight, true);
        Color topRightColor = new Color(topRight, true);
        drawRect(matrices, x, y, width, height, topLeftColor, bottomLeftColor, bottomRightColor, topRightColor);
    }

    public static void drawGradientQuad(MatrixStack matrices, float x, float y, float width, float height, int startColor, int endColor, boolean sideways) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        if (sideways) {
            bufferBuilder.vertex(posMatrix, x, y, 0.0F).color(f1, f2, f3, f);
            bufferBuilder.vertex(posMatrix, x, height, 0.0F).color(f1, f2, f3, f);
            bufferBuilder.vertex(posMatrix, width, height, 0.0F).color(f5, f6, f7, f4);
            bufferBuilder.vertex(posMatrix, width, y, 0.0F).color(f5, f6, f7, f4);
        } else {
            bufferBuilder.vertex(posMatrix, width, y, 0.0F).color(f1, f2, f3, f);
            bufferBuilder.vertex(posMatrix, x, y, 0.0F).color(f1, f2, f3, f);
            bufferBuilder.vertex(posMatrix, x, height, 0.0F).color(f5, f6, f7, f4);
            bufferBuilder.vertex(posMatrix, width, height, 0.0F).color(f5, f6, f7, f4);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawOutline(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(c.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(c.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(c.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void renderOutlineBox(MatrixStack stack, Box box, Color c, float lineWidth) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        setup3D();

        RenderSystem.lineWidth(lineWidth);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        WorldRenderer.drawBox(stack, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        end3D();
    }

    public static void drawOutlineBox(MatrixStack stack, Box box, Color c, float lineWidth) {
        if (!Manager.INSTANCE.antiAlias.getValue()) {
            renderOutlineBox(stack, box, c, lineWidth);
        } else {
            AntialiasingFramebuffer smoothBuffer = AntialiasingFramebuffer.getInstance();
            Framebuffer framebuffer = mc.getFramebuffer();
            AntialiasingFramebuffer.start(smoothBuffer, framebuffer);
            renderOutlineBox(stack, box, c, lineWidth);
            AntialiasingFramebuffer.end(smoothBuffer, framebuffer);
        }
    }

    public static void renderBox(MatrixStack stack, Box box, Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        end3D();
    }

    public static void drawBox(MatrixStack stack, Box box, Color c) {
        if (!Manager.INSTANCE.antiAlias.getValue()) {
            renderBox(stack, box, c);
        } else {
            AntialiasingFramebuffer smoothBuffer = AntialiasingFramebuffer.getInstance();
            Framebuffer framebuffer = mc.getFramebuffer();
            AntialiasingFramebuffer.start(smoothBuffer, framebuffer);
            renderBox(stack, box, c);
            AntialiasingFramebuffer.end(smoothBuffer, framebuffer);
        }
    }

    public static void drawLine(Vec3d start, Vec3d end, Color color) {
        drawLine(start.x, start.getY(), start.z, end.getX(), end.getY(), end.getZ(), color, 1.5f);
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, Color color, float width) {
        MatrixStack matrices = matrixFrom(x1, y1, z1);

        setup3D();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(width);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.lineWidth(1f);
        end3D();
    }

    public static void vertexLine(MatrixStack matrices, VertexConsumer buffer, double x1, double y1, double z1, double x2, double y2, double z2, Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Vector3f normalVec = getNormal((float) x1, (float) y1, (float) z1, (float) x2, (float) y2, (float) z2);

        buffer.vertex(model, (float) x1, (float) y1, (float) z1)
                .color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha())
                .normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
        buffer.vertex(model, (float) x2, (float) y2, (float) z2)
                .color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha())
                .normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
    }

    public static void drawUnfilledCircle(MatrixStack stack, double x, double y, double z, float radius, float lineWidth, int color) {
        setup3D();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        RenderSystem.lineWidth(lineWidth);

        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();
        for (int i = 0; i <= 360; i++) {
            bufferBuilder.vertex(matrix, (float) (x + Math.sin(i * Math.PI / 180.0) * radius), (float) (y + Math.cos(i * Math.PI / 180.0) * radius), 0.0F).color(color);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        stack.translate(-x, -y, -z);
        stack.pop();
        end3D();
    }

    public static void drawFilledCircle(MatrixStack stack, double x, double y, double z, float radius, int color) {
        setup3D();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();

        bufferBuilder.vertex(matrix, (float) x, (float) y, (float) z).color(color);

        for (int i = 0; i <= 360; i++) {
            bufferBuilder.vertex(matrix, (float) (x + Math.sin(i * Math.PI / 180.0) * radius), (float) (y + Math.cos(i * Math.PI / 180.0) * radius), (float) z).color(color);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        stack.pop();
        end3D();
    }

    public static Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;

        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    public static void drawOutlinedBox(MatrixStack stack, BlockPos pos, Color c, float lineWidth) {
        drawBox(stack, new Box(pos), c);
        drawOutlineBox(stack, new Box(pos), new Color(c.getRed(), c.getGreen(), c.getBlue(), 255), lineWidth);
    }

    public static void drawOutlinedBox(MatrixStack stack, Box box, Color c, float lineWidth) {
        drawBox(stack, box, c);
        drawOutlineBox(stack, box, new Color(c.getRed(), c.getGreen(), c.getBlue(), 255), lineWidth);
    }

    public static void drawOutlinedBox(MatrixStack stack, Box box, Color c, float alpha, float lineWidth) {
        drawBox(stack, box, c);
        drawOutlineBox(stack, box, new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha), lineWidth);
    }


    public static float interpolateFloat(float prev, float value, float factor) {
        return prev + ((value - prev) * factor);
    }

    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void setup3D() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
    }

    public static void end() {
        RenderSystem.disableBlend();
    }

    public static void end3D() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }


//    public static void glBillboard(float x2, float y2, float z2, float scale2) {
//        Camera camera = mc.gameRenderer.getCamera();
//        GlStateManager.translate((double) x2 - RenderUtil.mc.getRenderManager().viewerPosX, (double) y2 - RenderUtil.mc.getRenderManager().viewerPosY, (double) z2 - RenderUtil.mc.getRenderManager().viewerPosZ);
//        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
//        GlStateManager.rotate(-RenderUtil.mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
//        GlStateManager.rotate(RenderUtil.mc.player.rotationPitch, RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
//        GlStateManager.scale(-scale2, -scale2, scale2);
//    }
//
//    public static void glBillboardDistanceScaled(float x2, float y2, float z2, EntityPlayer player, float scale) {
//        glBillboard(x2, y2, z2, 0.01f * scale);
//        int distance = (int) player.getDistance(x2, y2, z2);
//        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
//        if (scaleDistance < 1.0f) {
//            scaleDistance = 1.0f;
//        }
//        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
//    }


    public static void drawText(String text, @NotNull Vec3d pos, double offX, double offY, double textOffset, @NotNull Color color) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = mc.gameRenderer.getCamera();

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));

        matrices.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y, pos.getZ() - camera.getPos().z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        setup3D();

        matrices.translate(offX, offY - 0.1, -0.01);
        matrices.scale(-0.025f, -0.025f, 0.0f);

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);

        FontManager.drawTextCentered(matrices, text, (int) textOffset, 0, color.getRGB());

        immediate.draw();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();

        end3D();
    }

    public static Vec3d getCameraPos() {
        Camera camera = mc.getBlockEntityRenderDispatcher().camera;
        if (camera == null)
            return Vec3d.ZERO;

        return camera.getPos();
    }

    public static Box interpolatePos(BlockPos pos) {
        return interpolatePos(pos, 1.0f);
    }

    public static Box interpolatePos(BlockPos pos, float height) {
        return new Box(pos.getX() - getCameraPos().x, pos.getY() - getCameraPos().y, pos.getZ() - getCameraPos().z, pos.getX() - getCameraPos().x + 1, pos.getY() - getCameraPos().y + height, pos.getZ() - getCameraPos().z + 1);
    }


    public static Vec3d interpolateEntity(Entity entity) {
        double x;
        double y;
        double z;
        {
            x = entity.prevX + (entity.getX() - entity.prevX) * mc.getRenderTickCounter().getTickDelta(true) - getCameraPos().x;
            y = entity.prevY + (entity.getY() - entity.prevY) * mc.getRenderTickCounter().getTickDelta(true) - getCameraPos().y;
            z = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getRenderTickCounter().getTickDelta(true) - getCameraPos().z;
        }

        return new Vec3d(x, y, z);
    }

    public static void prepareScissor(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, (context.getScaledWindowHeight() - height), (width - x), (height - y));
    }

    public static Box interpolate(Box bb) {
        return new Box(bb.minX - getCameraPos().x, bb.minY - getCameraPos().y, bb.minZ - getCameraPos().z, bb.maxX - getCameraPos().x, bb.maxY - getCameraPos().y, bb.maxZ - getCameraPos().z);
    }

    public static double interpolate(double previous, double current, double delta) {
        return previous + (current - previous) * delta;
    }


}
