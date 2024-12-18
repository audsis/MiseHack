package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.management.CommandManager;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;

public class Help extends Command {

    public Help() {
        super("Help", "Displays a list of commands", new String[]{"help", "hhh"});
    }

    @Override
    public void run(String[] cmd) {
        StringBuilder commandList = new StringBuilder("Commands: \n");

        for (Command command : CommandManager.INSTANCE.getCommands()) {
            commandList.append(command.getName())
                    .append(" - ")
                    .append(command.getDesc())
                    .append(";\n");
        }


        ChatUtils.sendMessage(new ChatMessage(commandList.toString(), false, 0));
    }
}
