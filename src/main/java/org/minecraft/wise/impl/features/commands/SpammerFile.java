package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.impl.features.modules.misc.Spammer;
import org.minecraft.loader.ModLoader;

import java.io.File;

public class SpammerFile extends Command {

    public SpammerFile() {
        super("SpammerFile", "Sets a file to spam.", new String[]{"spammerfile", "spammer"});
    }

    @Override
    public void run(String[] args) {

        if (args.length > 1) {
            File file = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + "spammer" + File.separator + args[1] + ".txt");
            if (file.exists()) {
                Spammer.INSTANCE.setCurrentFile(file);
                ChatUtils.sendMessage(new ChatMessage("Set spammer file to " + args[1] + ".txt", false, 0));
            } else {
                ChatUtils.sendMessage(new ChatMessage("Invalid file", false, 0));
            }
        } else {
            ChatUtils.sendMessage(new ChatMessage("Invalid format", false, 0));
        }
    }
}