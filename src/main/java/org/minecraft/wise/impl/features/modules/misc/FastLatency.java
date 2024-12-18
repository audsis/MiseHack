package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

public class FastLatency extends Module {
    public static FastLatency INSTANCE;
    private final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(1).withRange(0, 1000).register(this);
    private final Timer timer = new Timer.Single();
    public long responseTime;
    public int ping = 0;

    public FastLatency() {
        super("FastLatency", Category.Misc);
        INSTANCE = this;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (timer.hasPassed(delay.getValue().intValue())) {
            send(new RequestCommandCompletionsC2SPacket(1337, "w "));
            responseTime = System.currentTimeMillis();
            timer.reset();
        }
    }
}
