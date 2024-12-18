package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.event.TickEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TimeChanger extends Module {
    private final Value<Number> time = new ValueBuilder<Number>().withDescriptor("Time").withValue(12000).withRange(0, 24000).register(this);

    public TimeChanger() {
        super("TimeChanger", Category.Render);
        setDescription("You gain the power to control time.");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck()) return;

        mc.world.setTimeOfDay(time.getValue().longValue());
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
            event.cancel();
    }
}
