package org.minecraft.wise.api.utils.render.animations;

import net.minecraft.util.math.MathHelper;

public final class Easing {

    public static float linear(float time, float initial, float target, float duration) {
        return (time >= duration) ? initial + target : target * time / duration + initial;
    }

    public static float exponential(float time, float initial, float target, float duration) {
        return (time >= duration) ? initial + target : target * ((float) -Math.pow(2, -10 * time / duration) + 1) + initial;
    }

    public static float elastic(float time, float initial, float target, float duration) {
        if (time == 0) return initial;
        if ((time /= duration / 2) == 2) return initial + target;
        float a = 1.0F;
        float s;
        if (a < Math.abs(target)) {
            a = target;
            s = 1.0F / 4.0F;
        } else s = 1.0F / (float) (2 * Math.PI) * (float) Math.asin(target / a);
        if (time < 1)
            return -.5f * (a * (float) Math.pow(2, 10 * (time -= 1)) * MathHelper.sin((float) ((time * duration - s) * (2 * Math.PI)))) + initial;
        return a * (float) Math.pow(2, -10 * (time -= 1)) * MathHelper.sin((float) ((time * duration - s) * (2 * Math.PI))) * .5f + target + initial;
    }

    public static float bounce(float time, float initial, float target, float duration) {
        float s = 1.70158f;
        if (time > duration) {
            return initial + target;
        }
        return target * ((time = time / duration - 1) * time * ((s + 1) * time + s) + 1) + initial;
    }
}