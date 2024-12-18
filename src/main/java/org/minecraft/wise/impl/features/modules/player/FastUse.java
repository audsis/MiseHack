package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.mixin.mixins.access.IMinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastUse extends Module {

    public Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(0).withRange(0, 5).register(this);
    public Value<Boolean> blocks = new ValueBuilder<Boolean>().withDescriptor("Blocks").withValue(true).register(this);
    public Value<Boolean> crystals = new ValueBuilder<Boolean>().withDescriptor("Crystals").withValue(true).register(this);
    public Value<Boolean> xp = new ValueBuilder<Boolean>().withDescriptor("XP").withValue(true).register(this);
    public Value<Boolean> all = new ValueBuilder<Boolean>().withDescriptor("All").withValue(true).register(this);
    public Value<Boolean> fastBow = new ValueBuilder<Boolean>().withDescriptor("FastBow").withValue(true).register(this);
    private final Timer timer = new Timer.Single();

    public FastUse() {
        super("FastUse", Category.Misc);
    }

    @Override
    public String getHudInfo() {
        return delay.getValue().intValue() + "";
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (check(mc.player.getMainHandStack().getItem()) && ((IMinecraftClient) mc).getItemUseCooldown() > delay.getValue().intValue()) {
            ((IMinecraftClient) mc).setItemUseCooldown(delay.getValue().intValue());
        }

        if (mc.player.isHolding(Items.BOW)
                && mc.options.useKey.isPressed()
                && fastBow.getValue()
                && timer.hasPassed(delay.getValue().intValue() * 1000L)) {
            send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            timer.reset();
        }
    }

    public boolean check(Item item) {
        return item instanceof BlockItem && blocks.getValue() ||
                item == Items.END_CRYSTAL && crystals.getValue() ||
                item == Items.EXPERIENCE_BOTTLE && xp.getValue() ||
                all.getValue();
    }

}
