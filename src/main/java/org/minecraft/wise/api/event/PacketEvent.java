package org.minecraft.wise.api.event;


import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {
    public final Packet<?> packet;
    public final Time time;

    public PacketEvent(Packet<?> packet, Time time) {
        this.packet = packet;
        this.time = time;
    }

    public Time getTime() {
        return this.time;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public boolean isCancelable() {
        return true;
    }

    public enum Time {
        Send,
        Receive;
    }
}