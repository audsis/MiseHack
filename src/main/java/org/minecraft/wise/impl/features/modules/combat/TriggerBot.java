package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.TPSManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.mixin.mixins.access.ILivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

public class TriggerBot extends Module {
    private final Value<Boolean> others = new ValueBuilder<Boolean>().withDescriptor("Others").withValue(false).register(this);
    private final Value<Boolean> crystals = new ValueBuilder<Boolean>().withDescriptor("Crystals").withValue(false).register(this);
    private final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(20).withRange(0, 100).register(this);
    private final Value<Boolean> randomizer = new ValueBuilder<Boolean>().withDescriptor("Randomizer").withValue(false).register(this);
    private final Value<Number> randomAmount = new ValueBuilder<Number>().withDescriptor("RandomAmount").withValue(2).withRange(1, 5).register(this);
    private final Timer timer = new Timer.Single();

    public TriggerBot() {
        super("TriggerBot", Category.Combat);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (mc.crosshairTarget instanceof EntityHitResult gang) {

            if (crystals.getValue() && gang.getEntity() instanceof EndCrystalEntity) {
                send(PlayerInteractEntityC2SPacket.attack(gang.getEntity(), mc.player.isSneaking()));

                mc.player.swingHand(Hand.MAIN_HAND);
            }

            if (!others.getValue() && !(gang.getEntity() instanceof PlayerEntity))
                return;

            int delayer = delay.getValue().intValue();

            if (randomizer.getValue())
                delayer = ThreadLocalRandom.current().nextInt(delay.getValue().intValue() * randomAmount.getValue().intValue());

            if (timer.hasPassed((long) (getAttackCooldown() * 1000L + delayer))) {
                mc.interactionManager.attackEntity(mc.player, gang.getEntity());
                mc.player.swingHand(Hand.MAIN_HAND);

                timer.reset();
            }
        }
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0 / mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * (20.0 * TPSManager.INSTANCE.getTickFactor()));
    }

    public float getAttackCooldown() {
        int at = ((ILivingEntity) mc.player).getLastAttackedTicks();
        at = (int) (at * TPSManager.INSTANCE.getTickFactor());

        return MathHelper.clamp(((float) ((ILivingEntity) mc.player).getLastAttackedTicks()) / getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }
}
