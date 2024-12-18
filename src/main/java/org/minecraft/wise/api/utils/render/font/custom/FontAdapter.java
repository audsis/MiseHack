package org.minecraft.wise.api.utils.render.font.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public interface FontAdapter {
    void drawString(MatrixStack matrices, String text, float x, float y, int color);
    void drawString(DrawContext matrices, String text, float x, float y, int color);

    void drawString(MatrixStack matrices, String text, double x, double y, int color);

    void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a);

    void drawCenteredString(MatrixStack matrices, String text, double x, double y, int color);

    void drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a);

    float getWidth(String text);

    float getFontHeight();

    float getFontHeight(String text);

    float getMarginHeight();

    void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow);

    void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow);

    String trimStringToWidth(String in, double width);

    String trimStringToWidth(String in, double width, boolean reverse);
}
