package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.BreakEvent;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.math.MathUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;

public class AutoBreak extends Module {
    final Value<Number> range = new ValueBuilder<Number>().withDescriptor("Range").withValue(5.0f).withRange(0.0f, 10.0f).register(this);
    public final Value<Boolean> fast = new ValueBuilder<Boolean>().withDescriptor("Fast").withValue(true).register(this);
    public final Value<Boolean> rotate = new ValueBuilder<Boolean>().withDescriptor("Rotate").withValue(true).register(this);
    public final Value<Boolean> grim = new ValueBuilder<Boolean>().withDescriptor("Grim").withValue(true).register(this);
    public final Value<Color> color = new ValueBuilder<Color>().withDescriptor("Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Color> endColor = new ValueBuilder<Color>().withDescriptor("End Color").withValue(new Color(0, 150, 255)).register(this);
    private BlockPos selectedPos;
    private Direction direction;
    private boolean cool;
    private BlockState state;
    private float damage;

    public AutoBreak() {
        super("AutoBreak", Category.Misc);
    }

    @Override
    public String getHudInfo() {
        return String.format("%.1f", damage);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    @Subscribe
    public void onMineBlock(BreakEvent event) {
        BlockPos pos = event.getPos();
        Direction dir = event.getDir();

        if (!isBlockBreakable(pos)) return;

        if (selectedPos == null || !selectedPos.equals(pos)) {
            selectedPos = pos;
            direction = dir;
            send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, selectedPos, direction));
        }

        cool = true;
    }


    @Subscribe
    public void onPacketReceive(PacketEvent event) {
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet) {
            if (cool && packet.getPos().equals(selectedPos) && packet.getState() == mc.world.getBlockState(selectedPos)) {
                send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, selectedPos, direction));
                cool = false;
            }
        }
    }


    @Subscribe
    public void onTick(TickEvent event) {
        if (selectedPos == null) {
            damage = 0.0f;
            return;
        }

        state = mc.world.getBlockState(selectedPos);

        float delta = state.calcBlockBreakingDelta(mc.player, mc.world, selectedPos);
        damage += delta;

        if (state.isAir())
            damage = 0.0f;

        if (rotate.getValue()) {
            if (!(damage < 0.95))
                return;

            float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), selectedPos.toCenterPos());

            RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 3, false));
        }


        if (mc.player.squaredDistanceTo(selectedPos.toCenterPos()) <= MathUtil.square(range.getValue().floatValue())) {
            send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, selectedPos, direction));
            send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, selectedPos, direction));

            if (grim.getValue()) {
                send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, selectedPos.up(500), direction));
            }

            if (fast.getValue()) {
                send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, selectedPos, direction));
                send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, selectedPos, direction));
            }
        }
        else {
            reset();
        }

    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck() || selectedPos == null)
            return;

        if (state == null) {
            RenderUtils.drawBox(event.getMatrices(), new Box(selectedPos), ColorUtil.newAlpha(endColor.getValue(), 75));
            RenderUtils.drawOutlineBox(event.getMatrices(), new Box(selectedPos), ColorUtil.newAlpha(endColor.getValue(), 255), 1.0f);
            return;
        }

        VoxelShape outlineShape = state.getOutlineShape(mc.world, selectedPos);

        if (outlineShape.isEmpty())
            return;

        Box render1 = outlineShape.getBoundingBox();
        Box render = new Box(selectedPos.getX() + render1.minX, selectedPos.getY() + render1.minY, selectedPos.getZ() + render1.minZ, selectedPos.getX() + render1.maxX, selectedPos.getY() + render1.maxY, selectedPos.getZ() + render1.maxZ);
        Vec3d center = render.getCenter();
        float scale = MathHelper.clamp(damage, 0.0f, 1.0f);

        if (scale > 1.0f)
            scale = 1.0f;

        double dx = (render1.maxX - render1.minX) / 2.0;
        double dy = (render1.maxY - render1.minY) / 2.0;
        double dz = (render1.maxZ - render1.minZ) / 2.0;

        Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);

        RenderUtils.drawBox(event.getMatrices(), scaled, damage > 0.95f ? ColorUtil.newAlpha(color.getValue(), 75) : ColorUtil.newAlpha(endColor.getValue(), 75));
        RenderUtils.drawOutlineBox(event.getMatrices(), scaled, damage > 0.95f ? ColorUtil.newAlpha(color.getValue(), 255) : ColorUtil.newAlpha(endColor.getValue(), 255), 1.0f);
    }

    public void reset() {
        cool = false;
        direction = null;
        selectedPos = null;
        damage = 0.0f;
    }

    private boolean isBlockBreakable(BlockPos pos) {
        BlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();

        return block.getHardness() != -1.0F;
    }
}