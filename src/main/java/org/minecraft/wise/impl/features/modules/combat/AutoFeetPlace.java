package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.InventoryManager;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.utils.world.BlockUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.AntiCheat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AutoFeetPlace extends Module {
    private final Value<Number> range = new ValueBuilder<Number>().withDescriptor("Range").withValue(5).withRange(1, 10).register(this);
    public final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(0).withRange(0, 1000).register(this);
    public final Value<Boolean> predict = new ValueBuilder<Boolean>().withDescriptor("Predict").withValue(true).register(this);
    public final Value<Boolean> center = new ValueBuilder<Boolean>().withDescriptor("Center").withValue(false).register(this);
    public final Value<Boolean> centerBounds = new ValueBuilder<Boolean>().withDescriptor("CenterBounds").withValue(false).register(this);
    public final Value<Boolean> jumpDisable = new ValueBuilder<Boolean>().withDescriptor("JumpDisable").withValue(true).register(this);
    public final Value<Boolean> floor = new ValueBuilder<Boolean>().withDescriptor("Floor").withValue(true).register(this);
    public final Value<Boolean> extend = new ValueBuilder<Boolean>().withDescriptor("Extend").withValue(true).register(this);
    private final Timer timer = new Timer.Single();
    private int startY;

    public AutoFeetPlace() {
        super("AutoFeetPlace", Category.Combat);
        setDescription("Protects your feet from crystals.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();

        if (NullUtils.nullCheck()) return;

        if (center.getValue()) {
            double x2 = (double) mc.player.getBlockPos().getX() + 0.5;
            double z2 = (double) mc.player.getBlockPos().getZ() + 0.5;

            send(new PlayerMoveC2SPacket.PositionAndOnGround(x2, mc.player.getY(), z2, mc.player.isOnGround()));

            if (centerBounds.getValue()) {
                send(new PlayerMoveC2SPacket.PositionAndOnGround(x2, 1337.0, z2, mc.player.isOnGround()));
            }

            mc.player.setPosition(x2, mc.player.getY(), z2);
        }

        startY = (int) mc.player.getY();
    }

    @Subscribe
    public void onTick(TickEvent event) {

        if (NullUtils.nullCheck()) return;

        if (jumpDisable.getValue() && (mc.player.getY() != startY || !mc.player.isOnGround())) {
            setEnabled(false);
            return;
        }

        int prev = mc.player.getInventory().selectedSlot;
        int slot = getItemSlot(Items.OBSIDIAN);


        if (timer.hasPassed(delay.getValue().intValue())) {

            for (BlockPos pos : getOffsets(getPlayerPos())) {
                if (canPlaceBlock(pos)) continue;

                InventoryManager.INSTANCE.setSlot(slot);

                if (AntiCheat.INSTANCE.rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), pos.toCenterPos());

                    RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 6, false));
                }

                BlockUtils.placeBlock(pos, AntiCheat.INSTANCE.strictDirection.getValue(), true);

                InventoryManager.INSTANCE.setSlot(prev);
            }
            timer.reset();
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet && predict.getValue()) {
            BlockPos pos = packet.getPos();

            if (timer.hasPassed(delay.getValue().intValue())) {
                if (canPlaceBlock(pos)) return;

                if (AntiCheat.INSTANCE.rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), pos.toCenterPos());

                    RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 9, false));
                }

                BlockUtils.placeBlock(pos, AntiCheat.INSTANCE.strictDirection.getValue(), true);
            }
        }

        if (event.getPacket() instanceof PlaySoundS2CPacket packet && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            BlockPos pos = BlockPos.ofFloored(packet.getX(), packet.getY(), packet.getZ());

            if (timer.hasPassed(delay.getValue().intValue())) {
                if (canPlaceBlock(pos)) return;

                if (AntiCheat.INSTANCE.rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), pos.toCenterPos());

                    RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 9, false));
                }

                BlockUtils.placeBlock(pos, AntiCheat.INSTANCE.strictDirection.getValue(), true);
            }
        }
    }

    private boolean canPlaceBlock(BlockPos pos) {
        boolean allow = mc.world.getBlockState(pos).isReplaceable();

        for (Entity entity : mc.world.getEntitiesByClass(Entity.class, new Box(pos), e -> true)) {
            if (!(entity instanceof PlayerEntity)) continue;
            allow = false;
            break;
        }

        return !allow;
    }

    private List<BlockPos> getOffsets(BlockPos pos) {
        List<BlockPos> entities = getSurroundEntities(pos);
        List<BlockPos> blocks = new CopyOnWriteArrayList<>();

        for (BlockPos epos : entities) {
            for (Direction dir2 : Direction.values()) {
                if (!dir2.getAxis().isHorizontal())
                    continue;

                BlockPos pos2 = epos.add(dir2.getVector());

                if (entities.contains(pos2) || blocks.contains(pos2))
                    continue;

                double dist = mc.player.squaredDistanceTo(pos2.toCenterPos());

                if (dist > range.getValue().intValue())
                    continue;

                blocks.add(pos2);
            }
        }

        if (floor.getValue()) {
            for (BlockPos block : blocks) {
                Direction direction = BlockUtils.getInteractDirection(block, AntiCheat.INSTANCE.strictDirection.getValue());
                if (direction == null)
                    blocks.add(block.down());
            }
        }

        for (BlockPos entityPos : entities) {
            if (entityPos == pos)
                continue;

            blocks.add(entityPos.down());
        }

        Collections.reverse(blocks);
        return blocks;
    }

    public List<BlockPos> getSurroundEntities(BlockPos pos) {
        List<BlockPos> entities = new LinkedList<>();
        entities.add(pos);
        if (extend.getValue()) {
            for (Direction dir : Direction.values()) {
                if (!dir.getAxis().isHorizontal())
                    continue;

                BlockPos pos1 = pos.add(dir.getVector());
                List<Entity> box = mc.world.getOtherEntities(null, new Box(pos1)).stream().filter(e -> !isEntityBlockingSurround(e)).toList();

                if (box.isEmpty())
                    continue;

                for (Entity entity : box) {
                    entities.addAll(getAllInBox(entity.getBoundingBox(), pos));
                }
            }
        }
        return entities;
    }

    private boolean isEntityBlockingSurround(Entity entity) {
        return entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity;
    }

    public static List<BlockPos> getAllInBox(Box box, BlockPos pos) {
        List<BlockPos> intersections = new ArrayList<>();

        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                intersections.add(new BlockPos(x, pos.getY(), z));
            }
        }

        return intersections;
    }

    private BlockPos getPlayerPos() {
        double decimalPoint = mc.player.getY() - Math.floor(mc.player.getY());
        return new BlockPos((int) mc.player.getX(), (int) (decimalPoint > 0.8 ? Math.floor(mc.player.getY()) + 1.0 : Math.floor(mc.player.getY())), (int) mc.player.getZ());
    }

    public int getItemSlot(Item item) {
        if (mc.player == null)
            return 0;
        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            if (i != 0 && i != 5 && i != 6 && i != 7) {
                if (i != 8) {
                    ItemStack s = mc.player.getInventory().getStack(i);
                    if (!s.isEmpty()) {
                        if (s.getItem() == item) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }


}
