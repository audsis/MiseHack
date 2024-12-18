package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.mixin.mixins.access.ILivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;

public class FastClimb extends Module {

    private final Value<Number> speed = new ValueBuilder<Number>().withDescriptor("Speed").withValue(1.0f).withRange(0.0f, 2.0f).register(this);

    public FastClimb() {
        super("FastClimb", Category.Movement);
        setDescription("Climbs ladders faster than usual.");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (climbing()) {
            mc.player.setVelocity(mc.player.getVelocity().x, speed.getValue().intValue(), mc.player.getVelocity().z);
        }
    }

    private boolean climbing() {
        return (mc.player.horizontalCollision ||
                ((ILivingEntity) mc.player).isJumping()) &&
                (mc.player.isClimbing() || (mc.player.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(mc.player)));
    }
}
