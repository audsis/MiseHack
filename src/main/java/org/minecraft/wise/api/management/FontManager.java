package org.minecraft.wise.api.management;

import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.font.VanillaTextRenderer;
import org.minecraft.wise.api.utils.render.font.custom.CustomFont;
import org.minecraft.wise.api.utils.render.font.custom.FontAdapter;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.FontMod;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class FontManager implements IMinecraft {

    public static final VanillaTextRenderer VANILLA;
    public static FontManager INSTANCE;
    public static FontAdapter gang;
    public static FontAdapter neverloseSmall;
    public static FontAdapter neverloseBig;

    static {
        VANILLA = new VanillaTextRenderer();
    }

    public static @NotNull CustomFont createDefault() {
        return new CustomFont(new Font("Verdana", FontMod.INSTANCE.italic.getValue() ? Font.ITALIC : Font.PLAIN,
                FontMod.INSTANCE.fontSize.getValue().intValue() / 2),
                (float) FontMod.INSTANCE.fontSize.getValue().intValue() / 2);
    }

    public static @NotNull CustomFont create(String name, int size) throws IOException, FontFormatException {
        return new CustomFont(Font.createFont(Font.TRUETYPE_FONT,
                        Objects.requireNonNull(WiseMod.class.getClassLoader().getResourceAsStream("assets/minecraft/fonts/" + name + ".ttf")))
                .deriveFont(Font.PLAIN, size / 2f), size / 2f);
    }

    public FontManager() {
        Bus.EVENT_BUS.register(this);
    }

    public static void drawText(DrawContext stack, @Nullable String text, int x, int y, int color) {
        if (FontMod.INSTANCE.isEnabled()) {
            gang.drawString(stack, text, x, y, color);
        } else {
            VANILLA.drawWithShadow(stack.getMatrices(), text, (float) x, (float) y, color);
        }
    }

    public static void drawText(MatrixStack stack, @Nullable String text, int x, int y, int color) {
        if (FontMod.INSTANCE.isEnabled()) {
            gang.drawString(stack, text, x, y, color);
        } else {
            VANILLA.drawWithShadow(stack, text, (float) x, (float) y, color);
        }
    }

    public static void drawText(DrawContext stack, @Nullable String text, int x, int y, int color, boolean gradient) {
        if (gradient) {
            float xOffset = x;
            for (int i = 0; i < Objects.requireNonNull(text).length(); i++) {
                char c = text.charAt(i);
                int charWidth = getWidth(String.valueOf(c));

                if (FontMod.INSTANCE.isEnabled()) {
                    gang.drawString(stack.getMatrices(), text, x, y, color);
                } else {
                    VANILLA.drawWithShadow(stack.getMatrices(), String.valueOf(c), xOffset, y, hudColors(i).getRGB());
                }
                xOffset += charWidth;
            }
        } else {
            VANILLA.drawWithShadow(stack.getMatrices(), text, (float) x, (float) y, color);
        }
    }

    public static void drawText(MatrixStack stack, @Nullable String text, int x, int y, int color, boolean gradient) {
        if (gradient) {
            float xOffset = x;
            for (int i = 0; i < Objects.requireNonNull(text).length(); i++) {
                char c = text.charAt(i);
                int charWidth = getWidth(String.valueOf(c));

                if (FontMod.INSTANCE.isEnabled()) {
                    gang.drawString(stack, text, x, y, color);
                } else {
                    VANILLA.drawWithShadow(stack, String.valueOf(c), xOffset, y, hudColors(i).getRGB());
                }
                xOffset += charWidth;
            }
        } else {
            VANILLA.drawWithShadow(stack, text, (float) x, (float) y, color);
        }
    }

    public static void drawTextSmall(DrawContext context, @Nullable String text, int x, int y, int color) {
        neverloseSmall.drawString(context.getMatrices(), text, x, y, color, false);
    }

    public static void drawTextBig(DrawContext context, @Nullable String text, int x, int y, int color) {
        neverloseBig.drawString(context.getMatrices(), text, x, y, color, false);
    }

    public static int getWidthSmall(String text) {
        return (int) neverloseSmall.getWidth(text);
    }

    public static int getWidthBig(String text) {
        return (int) neverloseBig.getWidth(text);
    }

    public static int getHeightBig(String text) {
        return (int) neverloseBig.getFontHeight(text);
    }

    public static void drawTextCentered(MatrixStack stack, @Nullable String text, int x, int y, int color) {
        VANILLA.drawWithShadow(stack, text, (float) x - getWidth(text) / 2.0f, (float) y, color);
    }

    public static int getWidth(String text) {
        if (FontMod.INSTANCE.isEnabled()) {
            return (int) gang.getWidth(text);
        }
        return mc.textRenderer.getWidth(text);
    }

    public static int getHeight(String text) {
        Objects.requireNonNull(mc.textRenderer);
        if (FontMod.INSTANCE.isEnabled()) {
            return (int) gang.getFontHeight(text);
        }
        return 9;
    }

    private static Color hudColors(int cool) {
        double roundY = Math.sin(Math.toRadians(((long) cool * 10) + (double) System.currentTimeMillis() / (long) 15));
        roundY = Math.abs(roundY);

        if (HudColors.INSTANCE.colorMode.getValue().equals("Two Step Color")) {
            return ColorUtil.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), HudColors.INSTANCE.mainColor.getValue(), HudColors.INSTANCE.stepColor.getValue());
        } else if (HudColors.INSTANCE.colorMode.getValue().equals("Three Step Color")) {
            return ColorUtil.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), HudColors.INSTANCE.mainColor.getValue(), HudColors.INSTANCE.stepColor.getValue(), HudColors.INSTANCE.endColor.getValue());
        }

        return HudColors.INSTANCE.mainColor.getValue();
    }

}
