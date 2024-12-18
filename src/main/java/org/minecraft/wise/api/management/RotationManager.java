package org.minecraft.wise.api.management;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.*;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.AntiCheat;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class RotationManager implements IMinecraft {
    public static RotationManager INSTANCE;

    private float serverYaw, serverPitch, prevYaw, prevPitch;

    private final List<RotationPoint> points = new ArrayList<>();

    @Nullable
    private RotationPoint current;

    private boolean rotating;

    private int rotatedTicks;

    private float prevJumpYaw;

    public RotationManager() {
        Bus.EVENT_BUS.register(this);
    }

    @Subscribe
    public void onPacketSend(PacketEvent event) {
        if (NullUtils.nullCheck()) return;

        if (event.getPacket() instanceof PlayerMoveC2SPacket playerMoveC2SPacket && playerMoveC2SPacket.changesLook()) {
            serverYaw = playerMoveC2SPacket.getYaw(0.0f);
            serverPitch = playerMoveC2SPacket.getPitch(0.0f);
        }
    }

    public void update() {
        if (points.isEmpty()) {
            current = null;
            return;
        }

        RotationPoint prioritisedRotation = getPrioritisedRotation();

        if (prioritisedRotation == null && isHoldingTimeFinished()) {
            current = null;
            return;
        } else if (prioritisedRotation != null) {
            current = prioritisedRotation;
        }

        if (current == null) {
            return;
        }
        rotatedTicks = 0;
        rotating = true;
    }

    @Subscribe
    public void onLocation(LocationEvent event) {
        if (current != null && rotating) {
            points.remove(current);
            event.cancel();
            event.setYaw(current.getYaw());
            event.setPitch(current.getPitch());
            rotating = false;

            if (current.getInstant()) {
                current = null;
            }
        }
    }

    @Subscribe
    public void onRenderPlayerModel(RenderPlayerModelEvent event) {
        if (event.getEntity() == mc.player && current != null) {
            event.setYaw(RenderUtils.interpolateFloat(prevYaw, serverYaw, mc.getRenderTickCounter().getTickDelta(true)));
            event.setPitch(RenderUtils.interpolateFloat(prevPitch, serverPitch, mc.getRenderTickCounter().getTickDelta(true)));

            prevYaw = event.getYaw();
            prevPitch = event.getPitch();

            event.cancel();
        }
    }

    @Subscribe
    public void onKeyboardTick(KeyboardEvent event) {
        if (rotating && mc.player != null && AntiCheat.INSTANCE.moveFix.getValue()) {
            float forward = mc.player.input.movementForward;
            float sideways = mc.player.input.movementSideways;

            float delta = (mc.player.getYaw() - current.getYaw()) * MathHelper.RADIANS_PER_DEGREE;

            float cos = MathHelper.cos(delta);
            float sin = MathHelper.sin(delta);

            mc.player.input.movementSideways = Math.round(sideways * cos - forward * sin);
            mc.player.input.movementForward = Math.round(forward * cos + sideways * sin);
        }
    }

    @Subscribe
    public void onUpdateVelocity(VelocityEvent event) {
        if (rotating && AntiCheat.INSTANCE.moveFix.getValue()) {
            event.cancel();
            event.setVelocity(getMovementToVelocity(current.getYaw(), event.getMovementInput(), event.getSpeed()));
        }
    }

    @Subscribe
    public void onPlayerJump(JumpEvent event) {
        if (rotating && AntiCheat.INSTANCE.moveFix.getValue()) {
            prevJumpYaw = mc.player.getYaw();

            mc.player.setYaw(current.getYaw());

            mc.player.setYaw(prevJumpYaw);
        }
    }

    public void setRotationPoint(RotationPoint rotationPoint) {

        if (rotationPoint.getPriority() == Integer.MAX_VALUE) {
            current = rotationPoint;
        }

        RotationPoint toAdd = points.stream().filter(rp -> rotationPoint.getPriority() == rp.getPriority()).findFirst().orElse(null);

        if (toAdd == null) {
            points.add(rotationPoint);
        } else {
            toAdd.setYaw(rotationPoint.getYaw());
            toAdd.setPitch(rotationPoint.getPitch());
        }
    }

    private Vec3d getMovementToVelocity(float yaw, Vec3d input, float speed) {
        double d = input.lengthSquared();

        if (d < 1.0E-7)
            return Vec3d.ZERO;

        Vec3d vec3d = (d > 1.0 ? input.normalize() : input).multiply(speed);

        float f = MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE);
        float g = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE);

        return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) g + vec3d.x * (double) f);
    }

    private RotationPoint getPrioritisedRotation() {
        Optional<RotationPoint> rotationPoint = points.stream().max(Comparator.comparingInt(RotationPoint::getPriority));
        return rotationPoint.orElse(null);
    }

    public boolean isRotationLate(int priority) {
        return current != null && current.getPriority() > priority;
    }

    public boolean isRotating() {
        return rotating;
    }

    private boolean isHoldingTimeFinished() {
        return rotatedTicks > 0;
    }
}