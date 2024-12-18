package org.minecraft.wise.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class NametagsUtil implements IMinecraft {
    private static final Map<RegistryKey<Enchantment>, String> enchantmentKeyNames = new WeakHashMap<>(16);
    private static final Map<RegistryEntry<Enchantment>, String> enchantmentEntryNames = new Reference2ObjectOpenHashMap<>(16);

    public static void drawNametag(double x, double y, double z, String[] text, Color color, Color boxColor, Color lineColor, MatrixStack matrices) {
        Vec3d interpolation = RenderUtils.interpolateEntity(mc.player);

        double xDist = interpolation.x - x;
        double yDist = interpolation.y - y;
        double zDist = interpolation.z - z;

        double dist = MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
        double scale;
        int start;

        scale = 0.0018 + 0.003 * dist;
        if (dist <= 8.0)
            scale = 0.0245;
        start = -8;

        matrices.push();
        matrices.translate((float) x, (float) y, (float) z);
        Camera cam = mc.gameRenderer.getCamera();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cam.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.getPitch()));
        matrices.scale((float) (-scale), (float) (-scale), (float) (scale));



        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        double width = 0.0;

        for (String s : text) {
            double w2 = (double) FontManager.getWidth(s) / 2;
            if (!(w2 > width)) continue;
            width = w2;
        }

        float f = (float)(-width) - 2.0f;
        float f2 = (float)(width * 2) + 4.0f;
        RenderUtils.drawRect(matrices, f, 1.0f, f2, (float) (-9) - 1.0f, boxColor);

        float f4 = (float) (-width) - 2.0f;
        float f3 = (float) (width * 2) + 4.0f;
        RenderUtils.drawOutline(matrices, f4, 1.0f, f3, (float) (-9) - 1.0f, lineColor);

        for (int i3 = 0; i3 < text.length; ++i3) {
            FontManager.drawText(matrices, text[i3], -FontManager.getWidth(text[i3]) / 2, i3 + start, color.getRGB());
        }

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void drawBorderedRect(MatrixStack matrices, float x2, float x1, float lineWidth, Color inside, Color border) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderUtils.setup();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x2, -1.0f, 0.0f).color(inside.getRed(), inside.getGreen(), inside.getBlue(), inside.getAlpha());
        buffer.vertex(matrix, x1, -1.0f, 0.0f).color(inside.getRed(), inside.getGreen(), inside.getBlue(), inside.getAlpha());
        buffer.vertex(matrix, x1, 9.0f, 0.0f).color(inside.getRed(), inside.getGreen(), inside.getBlue(), inside.getAlpha());
        buffer.vertex(matrix, x2, 9.0f, 0.0f).color(inside.getRed(), inside.getGreen(), inside.getBlue(), inside.getAlpha());
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderUtils.end();
        RenderUtils.setup();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        RenderSystem.lineWidth(lineWidth);
        buffer.vertex(matrix, x2, 9.0f, 0.0f).color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        buffer.vertex(matrix, x2, -1.0f, 0.0f).color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        buffer.vertex(matrix, x1, -1.0f, 0.0f).color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        buffer.vertex(matrix, x1, 9.0f, 0.0f).color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        buffer.vertex(matrix, x2, 9.0f, 0.0f).color(border.getRed(), border.getGreen(), border.getBlue(), border.getAlpha());
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.lineWidth(1.0f);
        RenderUtils.end();
    }

    @SuppressWarnings("StringEquality")
    public static String get(RegistryKey<Enchantment> enchantment) {
        return enchantmentKeyNames.computeIfAbsent(enchantment, enchantment1 -> Optional.ofNullable(mc.getNetworkHandler())
                .map(ClientPlayNetworkHandler::getRegistryManager)
                .flatMap(registryManager -> registryManager.getOptional(RegistryKeys.ENCHANTMENT))
                .flatMap(registry -> registry.getEntry(enchantment))
                .map(NametagsUtil::get)
                .orElseGet(() -> {
                    String key = "enchantment." + enchantment1.getValue().toTranslationKey();
                    String translated = I18n.translate(key);
                    return translated == key ? enchantment1.getValue().toString() : translated;
                }));
    }

    public static String get(RegistryEntry<Enchantment> enchantment) {
        return enchantmentEntryNames.computeIfAbsent(enchantment, enchantment1 -> StringHelper.stripTextFormat(enchantment.value().description().getString()));
    }

}
