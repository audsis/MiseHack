package org.minecraft.wise.api.utils.world;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.minecraft.wise.api.utils.entity.EntityUtils;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CrystalUtil implements IMinecraft {
    public static final List<Integer> hitCrystals = new ArrayList<>();
    public static final List<BlockPos> placedPositions = new ArrayList<>();

    public static Crystal getPlacePos(Entity target, double range, double wallRange, boolean oneThirteen, double moveFactor, boolean antiSuicide, double antiSuicideFactor, double minDamage, double maxSelfDamage, boolean placeInhibit, boolean placeBlocks, int raytraceHits, double shrinkFactor) {
        ArrayList<Crystal> crystals = new ArrayList<>();
        for (BlockPos pos : CrystalUtil.getAvailablePositions(range, wallRange, oneThirteen, placeBlocks, raytraceHits, shrinkFactor)) {
            crystals.add(new Crystal(pos, target, moveFactor));
        }
        return crystals.stream().filter(crystal -> !placeInhibit || !placedPositions.contains(crystal.crystalPos))
                .filter(crystal -> (double) crystal.enemyDamage >= minDamage)
                .filter(crystal -> !antiSuicide || (double) crystal.selfDamage <= maxSelfDamage)
                .filter(crystal -> !antiSuicide || crystal.selfDamage <= crystal.enemyDamage && mc.player.getHealth() + mc.player.getAbsorptionAmount() - crystal.selfDamage > 0.0f)
                .max(Comparator.comparingDouble(crystal -> antiSuicide ? (double) crystal.enemyDamage - (double) crystal.selfDamage * antiSuicideFactor : (double) crystal.enemyDamage)).orElse(null);
    }


    public static List<BlockPos> getAvailablePositions(double range, double wallRange, boolean oneThirteen, boolean placeBlocks, int raytraceHits, double shrinkFactor) {
        return BlockUtils.getSphere(range, mc.player.getBlockPos(), true, false)
                .stream()
                .filter(pos -> canPlaceCrystal1(pos, oneThirteen, placeBlocks))
                .filter(CrystalUtil::canPlaceCrystal2)
                .filter(pos -> doSmartRaytrace(BlockUtils.getEyesPos(),
                        new Box((double) pos.getX() + 0.5 - 1.0,
                                pos.getY(),
                                (double) pos.getZ() + 0.5 - 1.0,
                                (double) pos.getX() + 0.5 + 1.0,
                                pos.getY() + 2,
                                (double) pos.getZ() + 0.5 + 1.0)
                                .shrink(shrinkFactor, shrinkFactor, shrinkFactor),
                        new Vec3d(mc.player.getX() + (double) (mc.player.getWidth() / 2.0f),
                                mc.player.getY(),
                                mc.player.getZ() + (double) (mc.player.getWidth() / 2.0f)), wallRange, raytraceHits))
                .collect(Collectors.toList());
    }

    public static Hand getCrystalHand() {
        return mc.player.getStackInHand(Hand.OFF_HAND).getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    public static EndCrystalEntity getCrystalToBreak(boolean inhibit, double range) {
        return (EndCrystalEntity)  Streams.stream(mc.world.getEntities())
                .filter(entity -> entity instanceof EndCrystalEntity)
                .filter(entity -> (double) mc.player.distanceTo(entity) <= range)
                .filter(entity -> !inhibit || !hitCrystals.contains(entity.getId()))
                .min(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)))
                .orElse(null);
    }

    public static Direction getPlaceDirection(BlockPos blockPos, boolean strictdir) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        if (strictdir) {
            if (mc.player.getY() >= blockPos.getY()) {
                return Direction.UP;
            }
            BlockHitResult result = mc.world.raycast(new RaycastContext(mc.player.getEyePos(), new Vec3d(x + 0.5, y + 0.5, z + 0.5), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                Direction direction = result.getSide();
                if (mc.world.isAir(blockPos.offset(direction))) {
                    return direction;
                }
            }
        } else {
            if (mc.world.isInBuildLimit(blockPos)) {
                return Direction.DOWN;
            }
            BlockHitResult result = mc.world.raycast(new RaycastContext(mc.player.getEyePos(), new Vec3d(x + 0.5, y + 0.5, z + 0.5), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                return result.getSide();
            }
        }
        return Direction.UP;
    }

    public static boolean canPlaceCrystal1(BlockPos pos, boolean one13, boolean placeBlocks) {
        return (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK ||
                placeBlocks && BlockUtils.canPlaceBlock(pos)) && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() == Blocks.AIR || one13);
    }

    public static boolean canPlaceCrystal2(BlockPos pos) {
        for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos.add(0, 1, 0)))) {
            if (!entity.isAlive() || entity instanceof EndCrystalEntity)
                continue;

            return false;
        }
        return true;
    }

    public static boolean doSmartRaytrace(Vec3d startPos, Box endBB, Vec3d playerPos, double wallRange, int hitCount) {
        boolean allow = false;
        int hits = 0;
        for (Vec3d pos : CrystalUtil.getSmartRaytraceVertex(endBB)) {
            HitResult result = mc.world.raycast(new RaycastContext(startPos, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            if (result != null && result.getType() == HitResult.Type.BLOCK) continue;
            ++hits;
        }
        if (hits >= hitCount) {
            allow = true;
        }
        if (!allow) {
            double centerX = (endBB.maxX - endBB.minX) / 2.0;
            double centerY = (endBB.maxY - endBB.minY) / 2.0;
            double centerZ = (endBB.maxZ - endBB.minZ) / 2.0;
            Vec3d vec3d = new Vec3d(endBB.minX + centerX, endBB.minY + centerY, endBB.minZ + centerZ);
            if (playerPos.distanceTo(vec3d) <= wallRange) {
                allow = true;
            }
        }
        return allow;
    }

    public static Vec3d[] getSmartRaytraceVertex(Box boundingBox) {
        double centerX = (boundingBox.maxX - boundingBox.minX) / 2.0;
        double centerY = (boundingBox.maxY - boundingBox.minY) / 2.0;
        double centerZ = (boundingBox.maxZ - boundingBox.minZ) / 2.0;

        return new Vec3d[]{new Vec3d(boundingBox.minX + centerX, boundingBox.minY + centerY, boundingBox.minZ + centerZ), new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)};
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, double moveFactor) {
        try {
            Vec3d applied = new Vec3d(entity.getX() + entity.getVelocity().x * moveFactor, entity.getY() + entity.getVelocity().y * moveFactor, entity.getZ() + entity.getVelocity().z * moveFactor);
            double factor = (1.0 - applied.distanceTo(new Vec3d(posX, posY, posZ)) / 12.0) * (double) getExposure(new Vec3d(posX, posY, posZ), entity.getBoundingBox());
            float calculatedDamage = (int) ((factor * factor + factor) / 2.0 * 7.0 * 12.0 + 1.0);
            double damage = 1.0;
            if (entity instanceof LivingEntity) {
                damage = CrystalUtil.getReduction(entity, mc.world.getDamageSources().explosion(null),
                        calculatedDamage * (mc.world.getDifficulty().getId() == 0 ? 0.0f : (mc.world.getDifficulty().getId() == 2 ? 1.0f : (mc.world.getDifficulty().getId() == 1 ? 0.5f : 1.5f))));
            }
            return (float) damage;
        } catch (Exception exception) {
            return 0.0f;
        }
    }


    private static double getReduction(Entity entity, DamageSource damageSource, double damage) {
        if (damageSource.isScaledWithDifficulty()) {
            switch (mc.world.getDifficulty()) {
                // case PEACEFUL -> return 0;
                case EASY -> damage = Math.min(damage / 2 + 1, damage);
                case HARD -> damage *= 1.5f;
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            damage = DamageUtil.getDamageLeft(livingEntity, (float) damage, damageSource, getArmor(livingEntity), (float) livingEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
            damage = getProtectionReduction(livingEntity, (float) damage, damageSource);
        }
        return Math.max(damage, 0);
    }

    private static float getArmor(LivingEntity entity) {
        return (float) Math.floor(entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
    }

    private static float getProtectionReduction(LivingEntity player, float damage, DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return damage;

        int damageProtection = 0;

        for (ItemStack stack : player.getAllArmorItems()) {
            Object2IntMap<RegistryEntry<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
            EntityUtils.getEnchantments(stack, enchantments);

            int protection = EntityUtils.getEnchantmentLevel(enchantments, Enchantments.PROTECTION);
            if (protection > 0) {
                damageProtection += protection;
            }

            int fireProtection = EntityUtils.getEnchantmentLevel(enchantments, Enchantments.FIRE_PROTECTION);
            if (fireProtection > 0 && source.isIn(DamageTypeTags.IS_FIRE)) {
                damageProtection += 2 * fireProtection;
            }

            int blastProtection = EntityUtils.getEnchantmentLevel(enchantments, Enchantments.BLAST_PROTECTION);
            if (blastProtection > 0 && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                damageProtection += 2 * blastProtection;
            }

            int projectileProtection = EntityUtils.getEnchantmentLevel(enchantments, Enchantments.PROJECTILE_PROTECTION);
            if (projectileProtection > 0 && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                damageProtection += 2 * projectileProtection;
            }

            int featherFalling = EntityUtils.getEnchantmentLevel(enchantments, Enchantments.FEATHER_FALLING);
            if (featherFalling > 0 && source.isIn(DamageTypeTags.IS_FALL)) {
                damageProtection += 3 * featherFalling;
            }
        }

        return DamageUtil.getInflictedDamage(damage, damageProtection);
    }

    private static float getExposure(Vec3d source, Box box) {
        RaycastFactory raycastFactory = getRaycastFactory(false);

        double xDiff = box.maxX - box.minX;
        double yDiff = box.maxY - box.minY;
        double zDiff = box.maxZ - box.minZ;

        double xStep = 1 / (xDiff * 2 + 1);
        double yStep = 1 / (yDiff * 2 + 1);
        double zStep = 1 / (zDiff * 2 + 1);

        if (xStep > 0 && yStep > 0 && zStep > 0) {
            int misses = 0;
            int hits = 0;

            double xOffset = (1 - Math.floor(1 / xStep) * xStep) * 0.5;
            double zOffset = (1 - Math.floor(1 / zStep) * zStep) * 0.5;

            xStep = xStep * xDiff;
            yStep = yStep * yDiff;
            zStep = zStep * zDiff;

            double startX = box.minX + xOffset;
            double startY = box.minY;
            double startZ = box.minZ + zOffset;
            double endX = box.maxX + xOffset;
            double endY = box.maxY;
            double endZ = box.maxZ + zOffset;

            for (double x = startX; x <= endX; x += xStep) {
                for (double y = startY; y <= endY; y += yStep) {
                    for (double z = startZ; z <= endZ; z += zStep) {
                        Vec3d position = new Vec3d(x, y, z);

                        if (raycast(new ExposureRaycastContext(position, source), raycastFactory) == null) misses++;

                        hits++;
                    }
                }
            }

            return (float) misses / hits;
        }

        return 0f;
    }

    private static RaycastFactory getRaycastFactory(boolean ignoreTerrain) {
        if (ignoreTerrain) {
            return (context, blockPos) -> {
                BlockState blockState = mc.world.getBlockState(blockPos);
                if (blockState.getBlock().getBlastResistance() < 600) return null;

                return blockState.getCollisionShape(mc.world, blockPos).raycast(context.start(), context.end(), blockPos);
            };
        } else {
            return (context, blockPos) -> {
                BlockState blockState = mc.world.getBlockState(blockPos);
                return blockState.getCollisionShape(mc.world, blockPos).raycast(context.start(), context.end(), blockPos);
            };
        }
    }

    private static BlockHitResult raycast(ExposureRaycastContext context, RaycastFactory raycastFactory) {
        return BlockView.raycast(context.start, context.end, context, raycastFactory, ctx -> null);
    }

    public static class Crystal {
        public final BlockPos crystalPos;
        public boolean blockUnder;
        float selfDamage;
        float enemyDamage;
        long startTime;

        public Crystal(BlockPos crystalPos, Entity target, double moveFactor) {
            this.crystalPos = crystalPos;
            calculate(target, moveFactor);
        }

        public float getSelfDamage() {
            return selfDamage;
        }

        public float getEnemyDamage() {
            return enemyDamage;
        }

        public long getStartTime() {
            return startTime;
        }

        public void calculate(Entity target, double moveFactor) {
            enemyDamage = calculateDamage((double) crystalPos.getX() + 0.5, (double) crystalPos.getY() + 1.0, (double) crystalPos.getZ() + 0.5, target, moveFactor);
            selfDamage = calculateDamage((double) crystalPos.getX() + 0.5, (double) crystalPos.getY() + 1.0, (double) crystalPos.getZ() + 0.5, mc.player, 0.0);
            startTime = System.currentTimeMillis();
        }

        public boolean equals(Object o2) {
            if (this == o2)
                return true;

            if (o2 == null || getClass() != o2.getClass())
                return false;

            Crystal crystal = (Crystal) o2;
            return crystalPos.equals(crystal.crystalPos);
        }

        public int hashCode() {
            return Objects.hash(crystalPos);
        }
    }

    public record ExposureRaycastContext(Vec3d start, Vec3d end) {}

    @FunctionalInterface
    public interface RaycastFactory extends BiFunction<ExposureRaycastContext, BlockPos, BlockHitResult> {}

}
