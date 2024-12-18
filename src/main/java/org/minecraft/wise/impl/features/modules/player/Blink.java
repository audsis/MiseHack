package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.util.LinkedList;
import java.util.Queue;

public class Blink extends Module {
    public static Blink INSTANCE;
    public final Value<Number> xPos = new ValueBuilder<Number>().withDescriptor("X Pos").withValue(100).withRange(0, 1000).register(this);
    public final Value<Number> yPos = new ValueBuilder<Number>().withDescriptor("Y Pos").withValue(10).withRange(0, 1000).register(this);
    final Queue<KeepAliveC2SPacket> packets = new LinkedList<>();
    final Queue<TeleportConfirmC2SPacket> tpPackets = new LinkedList<>();
    final Value<Boolean> cancel = new ValueBuilder<Boolean>().withDescriptor("Cancel").withValue(false).register(this);

    public Blink() {
        super("Blink", Feature.Category.Player);
        setDescription("Cancels packets to create a teleporting illusion");
        INSTANCE = this;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket && cancel.getValue()) {
            event.cancel();
        }
        if (event.getPacket() instanceof KeepAliveC2SPacket) {
            packets.add((KeepAliveC2SPacket) event.getPacket());
            event.cancel();
        }
        if (event.getPacket() instanceof TeleportConfirmC2SPacket) {
            tpPackets.add((TeleportConfirmC2SPacket) event.getPacket());
            event.cancel();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while (!packets.isEmpty()) {
            send(packets.poll());
        }
        while (!tpPackets.isEmpty()) {
            send(tpPackets.poll());
        }
    }

    @Subscribe
    public void onRender2d(Render2dEvent event) {
        xPos.setMax(event.getContext().getScaledWindowWidth());
        yPos.setMax(event.getContext().getScaledWindowHeight());
        FontManager.drawText(event.getContext(), "Currently Blinked", (int) xPos.getValue().floatValue(), (int) yPos.getValue().floatValue(), HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
    }
}
