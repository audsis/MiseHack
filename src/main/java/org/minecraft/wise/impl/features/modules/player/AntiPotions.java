package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.effect.StatusEffects;
import org.minecraft.wise.api.utils.NullUtils;

public class AntiPotions extends Module {

    public static AntiPotions INSTANCE;

    public final Value<Boolean> levitation = new ValueBuilder<Boolean>().withDescriptor("Levitation").withValue(true).register(this);
    public final Value<Boolean> miningFatigue = new ValueBuilder<Boolean>().withDescriptor("Mining Fatigue").withValue(true).register(this);
    public final Value<Boolean> jumpBoost = new ValueBuilder<Boolean>().withDescriptor("Jump Boost").withValue(true).register(this);
    public final Value<Boolean> slowness = new ValueBuilder<Boolean>().withDescriptor("Slowness").withValue(true).register(this);

    public AntiPotions() {
        super("AntiPotions", Category.Player);
        INSTANCE = this;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;
        if (mc.player.hasStatusEffect(StatusEffects.LEVITATION) && levitation.getValue()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
        }
        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE) && miningFatigue.getValue()) {
            mc.player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        }
        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS) && slowness.getValue()) {
            mc.player.removeStatusEffect(StatusEffects.SLOWNESS); 
        }
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) && jumpBoost.getValue()) {
            mc.player.removeStatusEffect(StatusEffects.JUMP_BOOST);
        }
    }
}
