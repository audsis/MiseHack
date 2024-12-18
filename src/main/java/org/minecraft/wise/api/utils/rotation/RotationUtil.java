package org.minecraft.wise.api.utils.rotation;

import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;


public class RotationUtil implements IMinecraft {
    public static Vec2f getRotationTo(Vec3d posTo, Vec3d posFrom) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    public static double normalizeAngle(Double angleIn) {
        double angle = angleIn;
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double xz = Math.hypot(vec.x, vec.z);
        float yaw = (float) normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitch = (float) normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2f(yaw, pitch);
    }

    public static float[] getRotationsTo(Vec3d src, Vec3d dest) {
        float yaw = (float) (Math.toDegrees(Math.atan2(dest.subtract(src).z, dest.subtract(src).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(dest.subtract(src).y, Math.hypot(dest.subtract(src).x, dest.subtract(src).z)));
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

}