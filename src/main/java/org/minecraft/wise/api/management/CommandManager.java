package org.minecraft.wise.api.management;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.bus.Bus;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    public static CommandManager INSTANCE;
    public final String PREFIX = "-";
    final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        Bus.EVENT_BUS.register(this);
    }

    @Subscribe
    public void onChat(PacketEvent event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket packet) {
            if (packet.chatMessage().startsWith(this.PREFIX)) {
                String sub = packet.chatMessage().substring(1);
                String[] args = sub.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (args.length > 0) {
                    block0:
                    for (Command command : this.commands) {
                        for (String s2 : command.getAlias()) {
                            if (!s2.equalsIgnoreCase(args[0])) continue;
                            command.run(args);
                            continue block0;
                        }
                    }
                } else {
                    //ChatUtils.sendMessage(new ChatMessage("Invalid command", false, 0));
                }
                event.cancel();
            }
        }
    }

    public List<Command> getCommands() {
        return this.commands;
    }
}