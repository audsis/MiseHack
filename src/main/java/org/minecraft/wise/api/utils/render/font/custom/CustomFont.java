package org.minecraft.wise.api.utils.render.font.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;


public class CustomFont implements FontAdapter {

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    final FontRenderer fontRenderer;

    final float si;

    public CustomFont(Font fnt, float si) {
        this.fontRenderer = new FontRenderer(fnt, si);
        this.si = si;
    }

    public float getSize() {
        return si;
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) {
            color1 |= 0xff000000;
        }
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawString(matrices, text, x, y, r, g, b, a);
    }

    @Override
    public void drawString(DrawContext matrices, String text, float x, float y, int color) {
        drawString(matrices.getMatrices(), text, x, y, color);
    }

    @Override
    public void drawString(MatrixStack matrices, String text, double x, double y, int color) {
        drawString(matrices, text, (float) x, (float) y, color);
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a) {
        float v = (int) (a * 255f) / 255f;
        fontRenderer.drawString(matrices, text, x, y - 3, r, g, b, v);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) {
            color1 |= 0xff000000;
        }
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawCenteredString(matrices, text, x, y, r, g, b, a);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a) {
        fontRenderer.drawCenteredString(matrices, text, (float) x, (float) y, r, g, b, a);
    }

    @Override
    public float getWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    @Override
    public float getFontHeight() {
        return fontRenderer.getStringHeight("abcdefg123");
    }

    @Override
    public float getFontHeight(String text) {
        return getFontHeight();
    }

    @Override
    public float getMarginHeight() {
        return getFontHeight();
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
//        drawString(matrices, s, x, y, color);
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) {
            color1 |= 0xff000000;
        }
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        fontRenderer.drawString(matrices, s, x, y, r, g, b, a, dropShadow);
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow) {
//        drawString(matrices, s, x, y, r, g, b, a);
        fontRenderer.drawString(matrices, s, x, y, r, g, b, a, dropShadow);
    }

    @Override


    public String trimStringToWidth(String in, double width) {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (getWidth(sb.toString() + c) >= width) {
                return sb.toString();
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override


    public String trimStringToWidth(String in, double width, boolean reverse) {
        return trimStringToWidth(in, width);
    }
}
