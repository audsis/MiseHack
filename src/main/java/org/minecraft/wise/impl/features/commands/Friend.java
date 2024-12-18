package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;

public class Friend extends Command {

    public Friend() {
        super("Friend", "Adds someone as a friend", new String[]{"friend", "friends"});
    }

    @Override
    public void run(String[] args) {
        if (args.length > 2) {
            if (args[1].equalsIgnoreCase("add")) {
                FriendManager.INSTANCE.getFriends().add(new org.minecraft.wise.api.friends.Friend(args[2]));
                ChatUtils.sendMessage(new ChatMessage("Added friend with ign: " + args[2], false, 0));
            } else if (args[1].equalsIgnoreCase("del")) {
                FriendManager.INSTANCE.getFriends().remove(new org.minecraft.wise.api.friends.Friend(args[2]));
                ChatUtils.sendMessage(new ChatMessage("Removed friend with ign: " + args[2], false, 0));
            } else {
                ChatUtils.sendMessage(new ChatMessage("Invalid format", false, 0));
            }
        } else {
            ChatUtils.sendMessage(new ChatMessage("Invalid format", false, 0));
        }
    }
}
