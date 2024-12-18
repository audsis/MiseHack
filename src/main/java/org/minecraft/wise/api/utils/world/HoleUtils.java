package org.minecraft.wise.api.utils.world;

import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class HoleUtils implements IMinecraft {

    public final static BlockPos[] holeOffsets = new BlockPos[]{
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    public static final BlockPos[] HOLE_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    public static final BlockPos[] AROUND_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

    public static boolean isHole(BlockPos pos) {
        return HoleUtils.isMixedHole(pos) || HoleUtils.isBedrockHole(pos) || HoleUtils.isObbyHole(pos);
    }

    public static boolean isInHole(PlayerEntity player) {
        return HoleUtils.isHole(player.getBlockPos());
    }

    public static boolean isWebHole(BlockPos pos) {
        if (!BlockUtils.isAir(pos.up()) || !BlockUtils.isAir(pos.up().up())) {
            return false;
        }
        for (BlockPos off : HOLE_OFFSETS) {
            if (HoleUtils.isWeb(pos.add(off))) continue;
            return false;
        }
        return true;
    }

    public static boolean isWeb(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isObbyHole(BlockPos pos) {
        if (!BlockUtils.isAir(pos.up()) || !BlockUtils.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HOLE_OFFSETS) {
            if (BlockUtils.isObby(pos.add(off))) continue;
            return false;
        }
        return true;
    }

    public static boolean isMixedHole(BlockPos pos) {
        if (HoleUtils.isBedrockHole(pos)) {
            return false;
        }
        if (!BlockUtils.isAir(pos.up()) || !BlockUtils.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HOLE_OFFSETS) {
            if (BlockUtils.isSafe(pos.add(off))) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos pos) {
        if (!BlockUtils.isAir(pos.up()) || !BlockUtils.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HOLE_OFFSETS) {
            if (BlockUtils.isBedrock(pos.add(off))) continue;
            return false;
        }
        return true;
    }

    public static boolean isTrapHole(BlockPos pos) {
        if (HoleUtils.isHole(pos) || !BlockUtils.isAir(pos.up()) || BlockUtils.isAir(pos.up(2)) || !BlockUtils.isSafe(pos.down())) {
            return false;
        }
        if (!BlockUtils.isSafe(pos.down())) {
            return false;
        }
        for (BlockPos off : AROUND_OFFSETS) {
            if (BlockUtils.isSafe(pos.up().add(off))) continue;
            return false;
        }
        return true;
    }

    public static boolean isTerrainHole(BlockPos pos) {
        if (HoleUtils.isHole(pos)) {
            return false;
        }
        if (!BlockUtils.isAir(pos.up()) || !BlockUtils.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HOLE_OFFSETS) {
            Block block = mc.world.getBlockState(pos.add(off)).getBlock();
            if (block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS || block == Blocks.NETHERRACK || block == Blocks.COBBLESTONE || block == Blocks.GRAVEL || block == Blocks.END_STONE || BlockUtils.isSafe(pos.add(off)))
                continue;
            return false;
        }
        return true;
    }

    public static boolean isDoubleHole(BlockPos pos) {
        Hole hole = HoleUtils.getDoubleHole(pos);
        if (hole == null) {
            return false;
        }
        return BlockUtils.isAir(hole.getFirst().up()) && BlockUtils.isAir(hole.getSecond().up()) && BlockUtils.isAir(hole.getFirst().up(2)) && BlockUtils.isAir(hole.getFirst().up(2));
    }

    public static Hole getDoubleHole(BlockPos pos) {
        if (HoleUtils.isBedrock(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), HoleTypes.BEDROCK);
        }
        if (HoleUtils.isBedrock(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), HoleTypes.BEDROCK);
        }
        if (HoleUtils.isObby(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), HoleTypes.OBBY);
        }
        if (HoleUtils.isObby(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), HoleTypes.OBBY);
        }
        if (HoleUtils.isMixed(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), HoleTypes.MIXED);
        }
        if (HoleUtils.isMixed(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), HoleTypes.MIXED);
        }
        return null;
    }

    public static Vec3d getCenter(Hole hole) {
        double x = (double) hole.getFirst().getX() + 0.5;
        double z = (double) hole.getFirst().getZ() + 0.5;
        if (hole.getSecond() != null) {
            x = (x + (double) hole.getSecond().getX() + 0.5) / 2.0;
            z = (z + (double) hole.getSecond().getZ() + 0.5) / 2.0;
        }
        return new Vec3d(x, hole.getFirst().getY(), z);
    }

    public static boolean isObby(BlockPos pos, int offX, int offZ) {
        return BlockUtils.isAir(pos) && BlockUtils.isAir(pos.add(offX, 0, offZ)) && BlockUtils.isObby(pos.add(0, -1, 0)) && BlockUtils.isObby(pos.add(offX, -1, offZ)) && BlockUtils.isObby(pos.add(offX * 2, 0, offZ * 2)) && BlockUtils.isObby(pos.add(-offX, 0, -offZ)) && BlockUtils.isObby(pos.add(offZ, 0, offX)) && BlockUtils.isObby(pos.add(-offZ, 0, -offX)) && BlockUtils.isObby(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtils.isObby(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static boolean isMixed(BlockPos pos, int offX, int offZ) {
        return BlockUtils.isAir(pos) && BlockUtils.isAir(pos.add(offX, 0, offZ)) && BlockUtils.isSafe(pos.add(0, -1, 0)) && BlockUtils.isSafe(pos.add(offX, -1, offZ)) && BlockUtils.isSafe(pos.add(offX * 2, 0, offZ * 2)) && BlockUtils.isSafe(pos.add(-offX, 0, -offZ)) && BlockUtils.isSafe(pos.add(offZ, 0, offX)) && BlockUtils.isSafe(pos.add(-offZ, 0, -offX)) && BlockUtils.isSafe(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtils.isSafe(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static boolean isBedrock(BlockPos pos, int offX, int offZ) {
        return BlockUtils.isAir(pos) && BlockUtils.isAir(pos.add(offX, 0, offZ)) && BlockUtils.isBedrock(pos.add(0, -1, 0)) && BlockUtils.isBedrock(pos.add(offX, -1, offZ)) && BlockUtils.isBedrock(pos.add(offX * 2, 0, offZ * 2)) && BlockUtils.isBedrock(pos.add(-offX, 0, -offZ)) && BlockUtils.isBedrock(pos.add(offZ, 0, offX)) && BlockUtils.isBedrock(pos.add(-offZ, 0, -offX)) && BlockUtils.isBedrock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtils.isBedrock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static List<Hole> getHoles(float range, boolean doubles, boolean webs, boolean trap, boolean terrain) {
        return HoleUtils.getHoles(mc.player, range, doubles, webs, trap, terrain);
    }

    public static List<Hole> getHoles(Entity player, float range, boolean doubles, boolean webs, boolean trap, boolean terrain) {
        List<Hole> holes = new ArrayList<>();
        for (BlockPos pos : BlockUtils.getSphere(player, range, false)) {
            if (!BlockUtils.isAir(pos)) continue;
            if (webs && HoleUtils.isWebHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.MIXED));
                continue;
            }
            if (HoleUtils.isBedrockHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.BEDROCK));
                continue;
            }
            if (HoleUtils.isObbyHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.OBBY));
                continue;
            }
            if (HoleUtils.isMixedHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.MIXED));
                continue;
            }
            if (trap && HoleUtils.isTrapHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.TRAPPED));
                continue;
            }
            if (terrain && HoleUtils.isTerrainHole(pos)) {
                holes.add(new Hole(pos, HoleTypes.TERRAIN));
                continue;
            }
            if (!doubles || !HoleUtils.isDoubleHole(pos)) continue;
            holes.add(HoleUtils.getDoubleHole(pos));
        }
        return holes;
    }

    public static class Hole {
        private BlockPos first;
        private BlockPos second;
        private final HoleTypes holeTypes;

        public Hole(BlockPos first, HoleTypes holeTypes) {
            this.first = first;
            this.holeTypes = holeTypes;
        }

        public Hole(BlockPos first, BlockPos second, HoleTypes holeTypes) {
            this.first = first;
            this.second = second;
            this.holeTypes = holeTypes;
        }

        public void setFirst(BlockPos first) {
            this.first = first;
        }

        public BlockPos getFirst() {
            return this.first;
        }

        public void setSecond(BlockPos second) {
            this.second = second;
        }

        public BlockPos getSecond() {
            return this.second;
        }

        public HoleTypes getHoleTypes() {
            return this.holeTypes;
        }

        public String toString() {
            return "Hole(" + this.first + ", " + this.second + ", " + (this.holeTypes) + ")";
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Hole hole)) {
                return false;
            }
            if (this.first != hole.first) {
                return false;
            }
            if (this.second != hole.second) {
                return false;
            }
            return this.holeTypes == hole.holeTypes;
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 31).append(this.first).append(this.first).toHashCode();
        }
    }

    public enum HoleTypes {
        BEDROCK,
        OBBY,
        MIXED,
        TERRAIN,
        TRAPPED
    }
}