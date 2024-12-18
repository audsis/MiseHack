package org.minecraft.wise.api.utils.player;

import org.minecraft.wise.api.event.MoveEvent;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.AntiCheat;
import org.minecraft.wise.api.ducks.IEntity;
import org.minecraft.wise.mixin.mixins.access.IMinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class PlayerUtils implements IMinecraft {

    @SuppressWarnings("all")
    public static double[] directionSpeed(double speed) {
        float forward = mc.player.input.movementForward;
        float side = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getRenderTickCounter().getTickDelta(true);

        if (forward != 0) {
            if (side > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (side < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

    public static double getSpeed(boolean slowness) {
        int amplifier;
        double defaultSpeed = 0.2873;
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        if (slowness && mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return defaultSpeed;
    }

    public static boolean movement() {
        //noinspection DataFlowIssue
        return mc.player.input.pressingLeft || mc.player.input.pressingRight || mc.player.input.pressingBack || mc.player.input.pressingForward || mc.player.input.sneaking;
    }

    public static double calcEffects(double speed) {
        int amplifier;
        double i = speed;
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            i *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            i /= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return i;
    }

    public static void strafe(MoveEvent event, double speed) {
        if (movement()) {
            double[] strafe = directionSpeed(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        } else {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static void movementFix() {

        if (!AntiCheat.INSTANCE.moveFix.getValue()) return;

        float forward = mc.player.input.movementForward;
        float sideways = mc.player.input.movementSideways;
        float delta = (mc.player.getYaw()) * MathHelper.RADIANS_PER_DEGREE;
        float cos = MathHelper.cos(delta);
        float sin = MathHelper.sin(delta);
        final float strafe = mc.player.input.movementSideways;
        final double angle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.bodyYaw, forward, strafe)));
        if (forward == 0 && strafe == 0) {
            return;
        }
        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = Math.round(sideways * cos - forward * sin);
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }
        mc.player.input.movementForward = closestForward;
        mc.player.input.movementSideways = closestStrafe;
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static boolean canFallFlying() {
        if (!mc.player.isOnGround() && !mc.player.isFallFlying() && !mc.player.isTouchingWater() && !mc.player.hasStatusEffect(StatusEffects.LEVITATION)) {
            ItemStack itemStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
                startFallFlying(true);
                return true;
            }
        }

        return false;
    }

    public static void startFallFlying(boolean client) {
        ((IEntity) mc.player).setFlag(7, client);
    }

    public static void stopFallFlying() {
        ((IEntity) mc.player).setFlag(7, true);
        ((IEntity) mc.player).setFlag(7, false);
    }

    public static void use() {
        ((IMinecraftClient) mc).doItemUse();
    }
}
