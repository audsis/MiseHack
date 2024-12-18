package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.*;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.InventoryManager;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.entity.TargetUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.utils.world.CrystalUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.AntiCheat;
import org.minecraft.wise.mixin.mixins.access.IInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

import static org.minecraft.wise.api.utils.world.CrystalUtil.hitCrystals;
import static org.minecraft.wise.api.utils.world.CrystalUtil.placedPositions;

public class CrystalAura extends Module {
    public static CrystalAura INSTANCE;
    private final Value<Number> minDamage = new ValueBuilder<Number>().withDescriptor("MinDamage").withValue(4.0).withRange(0.0, 20.0).register(this);
    private final Value<Number> maxSelfDamage = new ValueBuilder<Number>().withDescriptor("MaxSelfDamage").withValue(15.0).withRange(0.0, 36).register(this);
    private final Value<Boolean> antiSuicide = new ValueBuilder<Boolean>().withDescriptor("AntiSuicide").withValue(false).register(this);
    private final Value<Number> antiSuicideHealth = new ValueBuilder<Number>().withDescriptor("AntiSuicide Health").withValue(36).withRange(0, 36).register(this);
    private final Value<Number> antiSuicideFactor = new ValueBuilder<Number>().withDescriptor("AntiSuicide Factor").withValue(4).withRange(0, 10).register(this);
    private final Value<Number> lethalHealth = new ValueBuilder<Number>().withDescriptor("LethalHealth").withValue(18).withRange(0, 36).register(this);
    private final Value<Number> lethalMinDmg = new ValueBuilder<Number>().withDescriptor("LethalMinDamage").withValue(2).withRange(0, 36).register(this);
    private final Value<Number> lethalMaxSelfDmg = new ValueBuilder<Number>().withDescriptor("LethalMaxSelfDamage").withValue(36).withRange(0, 36).register(this);
    private final Value<Number> targetRange = new ValueBuilder<Number>().withDescriptor("TargetRange").withValue(7.0).withRange(3.0, 20.0).register(this);
    public final Value<Number> range = new ValueBuilder<Number>().withDescriptor("Range").withValue(5).withRange(1, 10).register(this);
    private final Value<Number> wallsRange = new ValueBuilder<Number>().withDescriptor("WallsRange").withValue(3).withRange(1, 5).register(this);
    private final Value<Number> raytraceHits = new ValueBuilder<Number>().withDescriptor("RaytraceHits").withValue(2).withRange(1, 9).register(this);
    private final Value<Number> shrinkFactor = new ValueBuilder<Number>().withDescriptor("ShrinkFactor").withValue(0.3).withRange(0, 1).register(this);
    private final Value<Number> breakDelay = new ValueBuilder<Number>().withDescriptor("BreakDelay").withValue(1).withRange(0, 1000).register(this);
    private final Value<Number> placeDelay = new ValueBuilder<Number>().withDescriptor("PlaceDelay").withValue(1).withRange(0, 1000).register(this);
    private final Value<Boolean> placeCrystals = new ValueBuilder<Boolean>().withDescriptor("Place").withValue(true).register(this);
    private final Value<String> swingMode = new ValueBuilder<String>().withDescriptor("SwingMode").withValue("Auto").withModes("Auto", "Mainhand", "Offhand", "None").register(this);
    private final Value<Boolean> one12 = new ValueBuilder<Boolean>().withDescriptor("1.12").withValue(false).register(this);
    private final Value<String> switchMode = new ValueBuilder<String>().withDescriptor("SwitchMode").withValue("None").withModes("None", "Normal", "Silent").register(this);
    private final Value<Boolean> breakCrystals = new ValueBuilder<Boolean>().withDescriptor("Break").withValue(true).register(this);
    private final Value<Number> breakAttempts = new ValueBuilder<Number>().withDescriptor("BreakAttempts").withValue(1).withRange(1, 10).register(this);
    private final Value<Boolean> setDead = new ValueBuilder<Boolean>().withDescriptor("SetDead").withValue(true).register(this);
    private final Value<Boolean> inhibit = new ValueBuilder<Boolean>().withDescriptor("Inhibit").withValue(true).register(this);
    private final Value<Boolean> await = new ValueBuilder<Boolean>().withDescriptor("Await").withValue(true).register(this);
    private final Value<Boolean> sequential = new ValueBuilder<Boolean>().withDescriptor("Sequential").withValue(true).register(this);

    private final Value<Boolean> idPredict = new ValueBuilder<Boolean>().withDescriptor("IDPredict").withValue(true).register(this);
    private final Value<Number> idOffset = new ValueBuilder<Number>().withDescriptor("IDOffset").withValue(1).withRange(1, 10).register(this);
    private final Value<Number> idPackets = new ValueBuilder<Number>().withDescriptor("IDPackets").withValue(1).withRange(1, 10).register(this);

    private final Value<Color> fillColor = new ValueBuilder<Color>().withDescriptor("FillColor").withValue(new Color(0, 0, 0, 100)).register(this);
    private final Value<Color> lineColor = new ValueBuilder<Color>().withDescriptor("OutlineColor").withValue(new Color(255, 255, 255, 255)).register(this);
    private final Value<String> renderMode = new ValueBuilder<String>().withDescriptor("RenderMode").withValue("Normal").withModes("Normal", "Glide").register(this);
    private final Value<Boolean> renderText = new ValueBuilder<Boolean>().withDescriptor("RenderText").withValue(false).register(this);
    private final Timer placeTimer = new Timer.Single();
    private final Timer breakTimer = new Timer.Single();
    private BlockPos renderPos;
    private CrystalUtil.Crystal placePos;
    private LivingEntity target;
    private int highestID = -100000;

    public CrystalAura() {
        super("CrystalAura", Category.Combat);
        INSTANCE = this;
        setDescription("automatically crystals ppl");
    }

    @Override
    public String getHudInfo() {
        return target != null ? target.getName().getString() + ", " + (double) breakTimer.getStartTime() / 10.0 : "";
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        target = TargetUtils.getTarget(targetRange.getValue().doubleValue());
    }

    @Subscribe
    public void onSync(SyncEvent event) {
        if (sequential.getValue()) {
            if (target == null)
                return;

            getBreak();
            getPlace();
        }
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (!sequential.getValue()) {
            if (target == null)
                return;

            if (!idPredict.getValue()) {
                getBreak();
            }

            Hand hand = swingMode.getValue().equals("None") ? null : (swingMode.getValue().equals("Auto") ? CrystalUtil.getCrystalHand() : (swingMode.getValue().equals("Offhand") ? Hand.OFF_HAND : Hand.MAIN_HAND));

            if (idPredict.getValue()) {
                updateEntityID();

                int id = highestID + 2;
                for (int i = 0; i < idPackets.getValue().intValue() * idOffset.getValue().intValue(); i += idOffset.getValue().intValue()) {
                    attackId(id + i, hand);
                }
            }

            getPlace();
        }

        if (target == null || placePos == null)
            return;

        if (mc.player.getStackInHand(CrystalUtil.getCrystalHand()).getItem() != Items.END_CRYSTAL && !switchMode.getValue().equals("Silent"))
            return;

        if (renderMode.getValue().equals("Normal")) {
            RenderUtils.drawBox(event.getMatrices(), new Box(placePos.crystalPos), fillColor.getValue());
            RenderUtils.drawOutlineBox(event.getMatrices(), new Box(placePos.crystalPos), lineColor.getValue(), 1.0f);
        }
    }

    @Subscribe
    public void onEntity(EntityAddEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (event.getEntity() instanceof EndCrystalEntity entity && await.getValue()) {
            if (breakTimer.hasPassed(breakDelay.getValue().longValue()) && breakCrystals.getValue()) {

                if (mc.player.distanceTo(entity) <= range.getValue().floatValue() || !hitCrystals.contains(entity.getId()))
                    return;

                if (AntiCheat.INSTANCE.rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), entity.getPos());

                    RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 9, false));
                }

                for (int i3 = 0; i3 < breakAttempts.getValue().intValue(); ++i3) {
                    Hand hand = swingMode.getValue().equals("None") ? null : (swingMode.getValue().equals("Auto") ? CrystalUtil.getCrystalHand() : (swingMode.getValue().equals("Offhand") ? Hand.OFF_HAND : Hand.MAIN_HAND));
                    breaking(entity, hand);
                }

                breakTimer.reset();
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) return;

        if (setDead.getValue()) {
            if (event.getPacket() instanceof PlaySoundS2CPacket packet && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : Lists.newArrayList(mc.world.getEntities())) {
                    if (entity instanceof EndCrystalEntity && entity.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 144.0) {
                        mc.executeSync(() -> mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED));
                    }
                }
            }

            if (event.getPacket() instanceof ExplosionS2CPacket explosion) {
                for (Entity entity : Lists.newArrayList(mc.world.getEntities())) {
                    if (entity instanceof EndCrystalEntity crystal && crystal.squaredDistanceTo(explosion.getX(), explosion.getY(), explosion.getZ()) <= 144 && crystal.isAlive()) {
                        mc.executeSync(() -> mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED));
                    }
                }
            }

            if (event.getPacket() instanceof EntitySpawnS2CPacket spawnPacket) {
                for (Entity ent : Lists.newArrayList(mc.world.getEntities())) {
                    if (ent instanceof EndCrystalEntity crystal && crystal.squaredDistanceTo(spawnPacket.getX(), spawnPacket.getY(), spawnPacket.getZ()) <= range.getValue().floatValue()) {
                        int entity = crystal.getId();
                        mc.executeSync(() -> {
                            mc.world.removeEntity(entity, Entity.RemovalReason.KILLED);
                            mc.world.removeBlockEntity(crystal.getBlockPos());
                        });
                    }
                }
            }
        }

        if (idPredict.getValue()) {
            if (event.getPacket() instanceof EntitySpawnS2CPacket spawn) {
                checkID(spawn.getEntityId());
            } else if (event.getPacket() instanceof ExperienceOrbSpawnS2CPacket spawn) {
                checkID(spawn.getEntityId());
            }

        }
    }

    public void getPlace() {
        if (placeTimer.hasPassed(Math.min(placeDelay.getValue().longValue(), 1))) {
            if (placeCrystals.getValue()) {

                boolean lethal = (double) (target.getHealth() + target.getAbsorptionAmount()) <= lethalHealth.getValue().doubleValue();

                placePos = CrystalUtil.getPlacePos(target, range.getValue().doubleValue(), wallsRange.getValue().doubleValue(), !one12.getValue(), 0.0,
                        antiSuicide.getValue() && (double) (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= antiSuicideHealth.getValue().doubleValue(),
                        antiSuicideFactor.getValue().doubleValue(), lethal ? lethalMinDmg.getValue().doubleValue() : minDamage.getValue().doubleValue(),
                        lethal ? lethalMaxSelfDmg.getValue().doubleValue() : maxSelfDamage.getValue().doubleValue(), false, false, raytraceHits.getValue().intValue(), shrinkFactor.getValue().doubleValue());

                Hand hand = swingMode.getValue().equals("None") ? null : (swingMode.getValue().equals("Auto") ? CrystalUtil.getCrystalHand() : (swingMode.getValue().equals("Offhand") ? Hand.OFF_HAND : Hand.MAIN_HAND));

                if (placePos != null) {
                    if (!switchMode.getValue().equals("None")) {
                        switch (switchMode.getValue()) {
                            case "Normal" -> {
                                int crystalSlot = getCrystalSlot();
                                if (crystalSlot != -1) {
                                    InventoryManager.INSTANCE.setClientSlot(crystalSlot);
                                }
                            }
                            case "Silent" -> {
                                int crystalSlot = getCrystalSlot();
                                if (crystalSlot != -1) {
                                    InventoryManager.INSTANCE.setSlot(crystalSlot);
                                }
                            }
                        }
                    }
                    if (AntiCheat.INSTANCE.rotate.getValue()) {
                        float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), placePos.crystalPos.toCenterPos().add(0.0, 0.5, 0.0));

                        RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 2, false));
                    }

                    BlockHitResult result = new BlockHitResult(placePos.crystalPos.toCenterPos(), CrystalUtil.getPlaceDirection(placePos.crystalPos, AntiCheat.INSTANCE.strictDirection.getValue()), placePos.crystalPos, false);

                    if (mc.player.getStackInHand(CrystalUtil.getCrystalHand()).getItem() == Items.END_CRYSTAL || switchMode.getValue().equals("Silent")) {
                        place(result, hand);
                    }

                    if (switchMode.getValue().equals("Silent")) {
                        InventoryManager.INSTANCE.syncToClient();
                    }

                    placedPositions.add(placePos.crystalPos);
                }
            }

            placeTimer.reset();
        }
    }

    public void getBreak() {
        if (breakTimer.hasPassed(Math.min(breakDelay.getValue().longValue(), 1)) && breakCrystals.getValue()) {
            EndCrystalEntity crystal = CrystalUtil.getCrystalToBreak(inhibit.getValue(), range.getValue().doubleValue());

            if (crystal == null || crystal.getType() == null)
                return;

            if (AntiCheat.INSTANCE.rotate.getValue()) {
                float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), crystal.getPos());

                RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 2, false));
            }

            for (int i3 = 0; i3 < breakAttempts.getValue().intValue(); ++i3) {
                Hand hand = swingMode.getValue().equals("None") ? null : (swingMode.getValue().equals("Auto") ? CrystalUtil.getCrystalHand() : (swingMode.getValue().equals("Offhand") ? Hand.OFF_HAND : Hand.MAIN_HAND));
                breaking(crystal, hand);
            }

            if (setDead.getValue())
                mc.world.removeEntity(crystal.getId(), Entity.RemovalReason.KILLED);


            breakTimer.reset();
        }
    }

    public void breaking(EndCrystalEntity crystal, Hand hand) {
        hand = hand != null ? hand : Hand.MAIN_HAND;

        send(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
        send(new HandSwingC2SPacket(hand));
        hitCrystals.add(crystal.getId());
        placedPositions.clear();
    }

    public void place(BlockHitResult result, Hand hand) {
        hand = hand != null ? hand : Hand.MAIN_HAND;
        Hand finalHand = hand;

        sendSeq(id -> new PlayerInteractBlockC2SPacket(finalHand, result, id));
        send(new HandSwingC2SPacket(hand));
    }

    public void updateEntityID() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity.getId() <= highestID) continue;
            highestID = entity.getId();
        }
    }

    private void checkID(int id) {
        if (id > highestID) {
            highestID = id;
        }
    }

    private void attackId(int id, Hand hand) {
        Entity entity = mc.world.getEntityById(id);
        if (entity == null || entity instanceof EndCrystalEntity) {
            PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(mc.player, mc.player.isSneaking());
            ((IInteractEntityC2SPacket) packet).setId(id);

            send(new HandSwingC2SPacket(hand));
        }
    }

    public void reset() {
        breakTimer.reset();
        placeTimer.reset();
        renderPos = null;
        placePos = null;
        target = null;
    }

    private int getCrystalSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof EndCrystalItem) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}