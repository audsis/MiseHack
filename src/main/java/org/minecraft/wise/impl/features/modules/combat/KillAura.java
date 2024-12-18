package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.management.InventoryManager;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.management.TPSManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.entity.EntityUtils;
import org.minecraft.wise.api.utils.entity.TargetUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.utils.rotation.RotationUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.AntiCheat;
import org.minecraft.wise.impl.features.modules.movement.Sprint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.Comparator;
import java.util.stream.Stream;

public class KillAura extends Module {
    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Single").withModes("Single", "Switch").register(this);
    private final Value<String> limb = new ValueBuilder<String>().withDescriptor("Limb").withValue("Auto").withModes("Head", "Feet", "Chest", "Auto").register(this);
    private final Value<String> priority = new ValueBuilder<String>().withDescriptor("Priority").withValue("Distance").withModes("Armor", "Distance", "Health").register(this);
    private final Value<Number> range = new ValueBuilder<Number>().withDescriptor("Range").withValue(5).withRange(1, 10).register(this);
    private final Value<Number> wallsRange = new ValueBuilder<Number>().withDescriptor("WallsRange").withValue(3).withRange(1, 6).register(this);
    private final Value<Number> targetRange = new ValueBuilder<Number>().withDescriptor("TargetRange").withValue(7.0).withRange(3.0, 20.0).register(this);
    private final Value<Boolean> players = new ValueBuilder<Boolean>().withDescriptor("Players").withValue(true).register(this);
    private final Value<Boolean> monsters = new ValueBuilder<Boolean>().withDescriptor("Monsters").withValue(true).register(this);
    private final Value<Boolean> passive = new ValueBuilder<Boolean>().withDescriptor("Passive").withValue(false).register(this);
    private final Value<Boolean> neutral = new ValueBuilder<Boolean>().withDescriptor("Neutral").withValue(false).register(this);
    private final Value<Boolean> stopShield = new ValueBuilder<Boolean>().withDescriptor("StopShield").withValue(false).register(this);
    private final Value<Boolean> stopSprint = new ValueBuilder<Boolean>().withDescriptor("StopSprint").withValue(false).register(this);
    private final Value<Boolean> one8 = new ValueBuilder<Boolean>().withDescriptor("1.8").withValue(false).register(this);
    private final Value<Boolean> naked = new ValueBuilder<Boolean>().withDescriptor("Naked").withValue(false).register(this);
    private final Value<Boolean> eatingPause = new ValueBuilder<Boolean>().withDescriptor("EatingPause").withValue(false).register(this);
    private final Value<Boolean> swap = new ValueBuilder<Boolean>().withDescriptor("Swap").withValue(false).register(this);
    private final Value<Color> fillColor = new ValueBuilder<Color>().withDescriptor("FillColor").withValue(new Color(255, 99, 99, 100)).register(this);
    private final Value<Color> lineColor = new ValueBuilder<Color>().withDescriptor("OutlineColor").withValue(new Color(255, 0, 0, 255)).register(this);
    private final Timer timer = new Timer.Single();
    private Entity target;

    public KillAura() {
        super("KillAura", Category.Combat);
        setDescription("Tries to commit devious acts with a sword.");
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        boolean sword = mc.player.getMainHandStack().getItem() instanceof SwordItem;

        if (sword) {
            if (target == null)
                return;


            RenderUtils.drawBox(event.getMatrices(), target.getBoundingBox(), fillColor.getValue());
            RenderUtils.drawOutlineBox(event.getMatrices(), target.getBoundingBox(), lineColor.getValue(), 1.0f);
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        boolean sword = mc.player.getMainHandStack().getItem() instanceof SwordItem;

        if (sword) {
            target = getTarget(mc.player.getEyePos());

            switch (mode.getValue()) {
                case "Switch" -> target = getTarget(mc.player.getEyePos());
                case "Single" -> {
                    if (target == null || !target.isAlive() || !isInRange(mc.player.getEyePos(), target))
                        target = getTarget(mc.player.getEyePos());
                }
            }

            if (target == null)
                return;

            if (swap.getValue()) {
                int slot = getItemSlot(getSword());

                if (slot != -1)
                    InventoryManager.INSTANCE.setClientSlot(slot);
            }

            if (AntiCheat.INSTANCE.rotate.getValue()) {
                float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), getLimbAF(target));

                RotationManager.INSTANCE.setRotationPoint(new RotationPoint(rotations[0], rotations[1], 1, false));
            }

            attack(target);
        }
    }

    public void attack(Entity entity) {
        setup();

        if (mc.player.isUsingItem() && eatingPause.getValue())
            return;

        float ticks = 20.0f - TPSManager.getInstance().getTickRate();
        float progress = mc.player.getAttackCooldownProgress(ticks);

        if (!one8.getValue()) {
            if (progress >= 1.0f) {
                if (isInRange(mc.player.getEyePos(), entity))
                    return;

                send(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

                mc.player.swingHand(Hand.MAIN_HAND);
                mc.player.resetLastAttackedTicks();
            }
        } else {
            if (isInRange(mc.player.getEyePos(), entity))
                return;

            send(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    public void setup() {
        boolean shield = mc.player.getOffHandStack().getItem() == Items.SHIELD && mc.player.isBlocking();
        boolean sprinting = mc.player.isSprinting() || mc.options.sprintKey.isPressed() || Sprint.INSTANCE.isEnabled();

        if (stopShield.getValue()) {
            if (shield)
                send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, mc.player.getBlockPos(), Direction.getFacing(mc.player.getX(), mc.player.getY(), mc.player.getZ())));
        }

        if (stopSprint.getValue()) {
            if (sprinting)
                send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    }

    public boolean isInRange(Vec3d eye, Entity entity) {
        if (eye.distanceTo(getLimbAF(entity)) <= range.getValue().floatValue())
            return false;

        BlockHitResult result = mc.world.raycast(new RaycastContext(eye, entity.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));

        return result == null || !(eye.distanceTo(getLimbAF(entity)) <= wallsRange.getValue().floatValue());
    }

    public Entity getTarget(Vec3d eye) {
        double min = Double.MAX_VALUE;

        for (Entity entity : mc.world.getEntities()) {

            if (!TargetUtils.isLiving(entity) || entity == mc.player || FriendManager.INSTANCE.isFriend(entity) || entity instanceof EndCrystalEntity
                    || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ItemEntity
                    || entity instanceof ItemFrameEntity || entity instanceof ArrowEntity || entity instanceof PlayerEntity player && player.isCreative()
                    || !isValid(entity))
                continue;

            if (!naked.getValue()) {
               if (entity instanceof LivingEntity && !((LivingEntity) entity).getArmorItems().iterator().hasNext())
                   continue;
            }

            if (!(eye.distanceTo(entity.getPos()) <= targetRange.getValue().floatValue()))
                continue;

            switch (priority.getValue()) {
                case "Health" -> {
                    if (entity instanceof LivingEntity e) {
                        float health = e.getHealth() + e.getAbsorptionAmount();
                        if (health < min) {
                            min = health;
                            return entity;
                        }
                    }
                }
                case "Armor" -> {
                    if (entity instanceof LivingEntity e) {
                        float armor = getArmorDurability(e);
                        if (armor < min) {
                            min = armor;
                            return entity;
                        }
                    }
                }
                case "Distance" -> {
                    if (eye.distanceTo(entity.getPos()) < min) {
                        min = eye.distanceTo(entity.getPos());
                        return entity;
                    }
                }
            }
        }

        return null;
    }

    private float getArmorDurability(LivingEntity livin) {
        float normal = 0;
        float max = 0;

        for (ItemStack armor : livin.getArmorItems()) {
            if (armor != null && !armor.isEmpty()) {
                normal += armor.getDamage();
                max += armor.getMaxDamage();
            }
        }

        return 100 - normal / max;
    }

    public Vec3d getLimbAF(Entity e) {
        return switch (limb.getValue()) {
            case "Chest" -> e.getPos().add(new Vec3d(0.0, e.getHeight() / 2, 0.0));
            case "Head" -> e.getEyePos();
            case "Auto" ->
                    Stream.of(e.getPos(),
                            e.getEyePos(),
                            e.getPos().add(new Vec3d(0.0, e.getHeight() / 2, 0.0))).min(Comparator.comparing(b -> mc.player.getEyePos().squaredDistanceTo(b))).orElse(e.getEyePos());
            case "Feet" -> e.getPos();
            default -> throw new IllegalStateException("Unexpected value: " + limb.getValue());
        };
    }

    private boolean isValid(Entity entity) {
        return entity instanceof PlayerEntity && players.getValue() ||
                EntityUtils.isMonster(entity) && monsters.getValue() ||
                EntityUtils.isPassive(entity) && passive.getValue() ||
                EntityUtils.isNeutral(entity) && neutral.getValue();
    }

    public Item getSword() {
        if (getItemCount(Items.NETHERITE_SWORD) == -1) return Items.DIAMOND_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1)
            return Items.IRON_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1 && getItemCount(Items.IRON_SWORD) == -1)
            return Items.STONE_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1 && getItemCount(Items.IRON_SWORD) == -1 && getItemCount(Items.STONE_SWORD) == -1)
            return Items.WOODEN_SWORD;

        return Items.NETHERITE_SWORD;
    }

    public int getItemCount(Item item) {
        if (mc.player == null) return 0;

        int counter = 0;

        for (int i = 0; i <= 44; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            counter += itemStack.getCount();
        }

        return counter;
    }

    public int getItemSlot(Item item) {

        if (mc.player == null) return 0;

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


    public void reset() {
        target = null;
        timer.reset();
    }
}
