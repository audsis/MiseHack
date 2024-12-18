package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.EntityMovementEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.InventoryManager;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.utils.player.InventoryUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Phase extends Module {
    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Pearl").withModes("Pearl", "Normal").register(this);

    public Phase() {
        super("Phase", Category.Player);
        setDescription("Phases you inside of blocks using pearls.");
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (NullUtils.nullCheck()) {
            setEnabled(false);
            return;
        }

        if (mode.getValue().equals("Pearl")) {

            if (mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL))
                setEnabled(false);

            float pitch = mc.player.getPitch();
            float yaw = mc.player.getYaw();

            float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), new Vec3d(Math.floor(mc.player.getX()) + 0.5, 0.0, Math.floor(mc.player.getZ()) + 0.5));

            RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 1000, false));

            int pearlSlot = InventoryUtils.getHotbarItemSlot(Items.ENDER_PEARL);
            int oldSlot = mc.player.getInventory().selectedSlot;

            if (pearlSlot == -1) {
                ChatUtils.sendMessage(new ChatMessage("No pearls in hotbar", false, 0));
                setEnabled(false);
                return;
            }

            InventoryManager.INSTANCE.setSlot(pearlSlot);

            send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), rotations[0] + 180.0f, 85, mc.player.isOnGround()));
            sendSeq(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, rotations[0] + 180.0f, 85));
            mc.player.swingHand(Hand.MAIN_HAND);

            InventoryManager.INSTANCE.setSlot(oldSlot);

            RotationManager.INSTANCE.setRotationPoint(new RotationPoint(yaw, pitch, 9, false));
            setEnabled(false);
        }

    }

    @Subscribe
    public void onMove(EntityMovementEvent event) {
        if (NullUtils.nullCheck()) {
            setEnabled(false);
            return;
        }

        if (mode.getValue().equals("Normal")) {

            if (!(mc.player.horizontalCollision))
                return;

            double horizontal = event.getHorizontalLength() / 0.06;

            double x = event.getX() / horizontal;
            double z = event.getZ() / horizontal;

            Vec3d awesome = mc.player.getPos().add(x, event.getY(), z).withAxis(Direction.Axis.Y, mc.player.getY());

            send(new PlayerMoveC2SPacket.PositionAndOnGround(awesome.x, awesome.y, awesome.z, mc.player.isOnGround()));
            send(new PlayerMoveC2SPacket.PositionAndOnGround(awesome.x, awesome.y - 87.0, awesome.z, mc.player.isOnGround()));

            mc.player.setPosition(awesome);

            event.setX(0.0);
            event.setZ(0.0);
        }
    }


}
