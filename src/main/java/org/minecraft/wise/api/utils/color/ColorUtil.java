package org.minecraft.wise.api.utils.color;

import org.minecraft.wise.api.utils.math.MathUtil;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ColorUtil {
    public static Color newAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color newAlpha(int color2, int alpha) {
        Color color = new Color(color2);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color interpolate(float value, Color start, Color end) {
        float sr = start.getRed() / 255.0f;
        float sg = start.getGreen() / 255.0f;
        float sb = start.getBlue() / 255.0f;
        float sa = start.getAlpha() / 255.0f;

        float er = end.getRed() / 255.0f;
        float eg = end.getGreen() / 255.0f;
        float eb = end.getBlue() / 255.0f;
        float ea = end.getAlpha() / 255.0f;

        float r = sr * value + er * (1.0f - value);
        float g = sg * value + eg * (1.0f - value);
        float b = sb * value + eb * (1.0f - value);
        float a = sa * value + ea * (1.0f - value);

        return new Color(r, g, b, a);
    }

    public static int getRGBA(Color color) {
        if (color.getAlpha() <= 0) {
            return 0x00;
        }
        return getRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static Color interpolate(float value, Color start, Color middle, Color end) {
        if (value < 0.5f) {
            return interpolate((float) MathHelper.clamp(MathUtil.normalize(value, 0.0, 0.5), 0.0, 1.0), middle, start);
        }

        return interpolate((float) MathHelper.clamp(MathUtil.normalize(value, 0.5, 1.0), 0.0, 1.0), end, middle);
    }

    public static Color rainbow(int delay, int index, float saturation, float brightness) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 16.0f) + index;
        rainbow %= 360.0;

        return Color.getHSBColor((float) (rainbow / 360.0), saturation, brightness);
    }


    public static Color rainbow(int delay, float saturation, float brightness) {
        double rainbow = Math.ceil((float) (System.currentTimeMillis() + (long) delay) / 16.0f);
        return Color.getHSBColor((float) ((rainbow %= 360.0) / 360.0), saturation, brightness);
    }

    public static Color hslToColor(float f, float f2, float f3, final float f4) {
        if (f2 < 0.0f || f2 > 100.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
        }

        if (f3 < 0.0f || f3 > 100.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Lightness");
        }

        if (f4 < 0.0f || f4 > 1.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
        }

        f %= 360.0f;

        float f5;
        f5 = ((f3 < 0.5) ? (f3 * (1.0f + f2)) : ((f3 /= 100.0f) + (f2 /= 100.0f) - f2 * f3));

        f2 = 2.0f * f3 - f5;
        f3 = Math.max(0.0f, colorCalc(f2, f5, (f /= 360.0f) + 0.33333334f));

        float f6 = Math.max(0.0f, colorCalc(f2, f5, f));

        f2 = Math.max(0.0f, colorCalc(f2, f5, f - 0.33333334f));
        f3 = Math.min(f3, 1.0f);
        f6 = Math.min(f6, 1.0f);
        f2 = Math.min(f2, 1.0f);

        return new Color(f3, f6, f2, f4);
    }

    private static float colorCalc(final float f, final float f2, float f3) {
        if (f3 < 0.0f) {
            ++f3;
        }
        if (f3 > 1.0f) {
            --f3;
        }
        if (6.0f * f3 < 1.0f) {
            final float f4 = f;
            return f4 + (f2 - f4) * 6.0f * f3;
        }
        if (2.0f * f3 < 1.0f) {
            return f2;
        }
        if (3.0f * f3 < 2.0f) {
            final float f5 = f;
            return f5 + (f2 - f5) * 6.0f * (0.6666667f - f3);
        }
        return f;
    }
}