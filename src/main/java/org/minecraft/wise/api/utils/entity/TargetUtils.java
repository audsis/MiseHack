package org.minecraft.wise.api.utils.entity;

import com.google.common.collect.Streams;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;
import java.util.Objects;

public class TargetUtils implements IMinecraft {

    public static LivingEntity getTarget(double targetRange) {
        return (LivingEntity) Streams.stream(mc.world.getEntities()).
                filter(Objects::nonNull).
                filter(entity -> entity instanceof PlayerEntity).
                filter(TargetUtils::isAlive).
                filter(entity -> entity.getId() != mc.player.getId()).
                filter(entity -> !FriendManager.INSTANCE.isFriend(entity)).
                filter(entity -> mc.player.distanceTo(entity) <= targetRange).
                min(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity))).orElse(null);
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && entity.isAlive() && ((LivingEntity) entity).getHealth() > 0.0f;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity;
    }
}
