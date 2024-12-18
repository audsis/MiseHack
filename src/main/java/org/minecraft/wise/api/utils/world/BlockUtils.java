package org.minecraft.wise.api.utils.world;

import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;

import java.util.*;

public class BlockUtils implements IMinecraft {

    public static boolean placeBlock(final BlockPos pos, final boolean strictDirection, final boolean clientSwing) {
        Direction direction = getInteractDirection(pos, strictDirection);

        if (direction == null) {
            return false;
        }

        final BlockPos neighbor = pos.offset(direction.getOpposite());
        return placeBlock(neighbor, direction, clientSwing);
    }

    public static boolean placeBlock(final BlockPos pos, final Direction direction, final boolean clientSwing) {
        Vec3d hitVec = pos.toCenterPos().add(new Vec3d(direction.getUnitVector()).multiply(0.5));
        return placeBlock(new BlockHitResult(hitVec, direction, pos, false), clientSwing);
    }

    public static boolean placeBlock(final BlockHitResult hitResult, final boolean clientSwing) {
        return placeBlockImmediately(hitResult, clientSwing);
    }

    public static boolean placeBlockImmediately(final BlockHitResult result, final boolean clientSwing) {
        final ActionResult actionResult = placeBlockInternally(result);

        if (mc.getNetworkHandler() == null) return false;

        if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
            if (clientSwing) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            else {
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
        }

        return actionResult.isAccepted();
    }

    private static ActionResult placeBlockInternally(final BlockHitResult hitResult) {
        return mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
    }

    public static Direction getInteractDirection(final BlockPos blockPos, final boolean strictDirection)
    {
        Set<Direction> ncpDirections = getPlaceDirectionsNCP(mc.player.getEyePos(), blockPos.toCenterPos());
        Direction interactDirection = null;
        for (final Direction direction : Direction.values())
        {
            final BlockState state = mc.world.getBlockState(blockPos.offset(direction));
            if (state.isAir() || !state.getFluidState().isEmpty())
            {
                continue;
            }
            if (strictDirection && !ncpDirections.contains(direction.getOpposite()))
            {
                continue;
            }
            interactDirection = direction;
            break;
        }
        if (interactDirection == null)
        {
            return null;
        }
        return interactDirection.getOpposite();
    }

    public static Set<Direction> getPlaceDirectionsNCP(Vec3d eyePos, Vec3d blockPos) {
        return getPlaceDirectionsNCP(eyePos.x, eyePos.y, eyePos.z, blockPos.x, blockPos.y, blockPos.z);
    }

    public static Set<Direction> getPlaceDirectionsNCP(final double x, final double y, final double z, final double dx, final double dy, final double dz) {
        final double xdiff = x - dx;
        final double ydiff = y - dy;
        final double zdiff = z - dz;
        final Set<Direction> dirs = new HashSet<>(6);
        if (ydiff > 0.5) {
            dirs.add(Direction.UP);
        } else if (ydiff < -0.5) {
            dirs.add(Direction.DOWN);
        } else {
            dirs.add(Direction.UP);
            dirs.add(Direction.DOWN);
        }
        if (xdiff > 0.5) {
            dirs.add(Direction.EAST);
        } else if (xdiff < -0.5) {
            dirs.add(Direction.WEST);
        } else {
            dirs.add(Direction.EAST);
            dirs.add(Direction.WEST);
        }
        if (zdiff > 0.5) {
            dirs.add(Direction.SOUTH);
        } else if (zdiff < -0.5) {
            dirs.add(Direction.NORTH);
        } else {
            dirs.add(Direction.SOUTH);
            dirs.add(Direction.NORTH);
        }
        return dirs;
    }


    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
    }

    public static List<BlockPos> getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<>();
        int cx2 = pos.getX();
        int cy2 = pos.getY();
        int cz2 = pos.getZ();
        int x2 = cx2 - (int) range;
        while ((double) x2 <= (double) cx2 + range) {
            int z2 = cz2 - (int) range;
            while ((double) z2 <= (double) cz2 + range) {
                int y2 = sphere ? cy2 - (int) range : cy2;
                while (true) {
                    double d2 = y2;
                    double d3 = (double) cy2 + range;
                    if (!(d2 < d3)) break;
                    double dist = (cx2 - x2) * (cx2 - x2) + (cz2 - z2) * (cz2 - z2) + (sphere ? (cy2 - y2) * (cy2 - y2) : 0);
                    if (!(!(dist < range * range) || hollow && dist < (range - 1.0) * (range - 1.0))) {
                        BlockPos l2 = new BlockPos(x2, y2, z2);
                        circleblocks.add(l2);
                    }
                    ++y2;
                }
                ++z2;
            }
            ++x2;
        }
        return circleblocks;
    }

    public static boolean areBoxesEmpty(final BlockPos pos) {
        return mc.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).isEmpty();
    }


    public static boolean canPlaceBlock(final BlockPos pos) {
        boolean allow = mc.world.getBlockState(pos).isReplaceable();
        if (!areBoxesEmpty(pos)) {
            allow = false;
        }
        return allow;
    }

    public static boolean isAir(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    public static boolean isObby(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public static boolean isBedrock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isEchest(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    public static boolean isSafe(BlockPos pos) {
        return isObby(pos) || isBedrock(pos) || isEchest(pos);
    }

    public static List<BlockPos> getSphere(Entity entity, float radius, boolean ignoreAir) {
        List<BlockPos> sphere = new ArrayList<>();

        BlockPos pos = entity.getBlockPos();

        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        int radiuss = (int) radius;

        for (int x = posX - radiuss; x <= posX + radius; x++) {
            for (int z = posZ - radiuss; z <= posZ + radius; z++) {
                for (int y = posY - radiuss; y < posY + radius; y++) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (ignoreAir && mc.world.getBlockState(position).getBlock() == Blocks.AIR) {
                            continue;
                        }
                        sphere.add(position);
                    }
                }
            }
        }

        return sphere;
    }


}
