package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.wrapper.IMinecraft;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Coordinates extends Command implements IMinecraft {

    public Coordinates() {
        super("Coordinates", "puts coords on ur clipboard", new String[]{"coords", "coordinates"});
    }

    @Override
    public void run(String[] args) {
        StringSelection selection = new StringSelection("XYZ: " + mc.player.getX() + ", " + mc.player.getY() + ", " + mc.player.getZ());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        ChatUtils.sendMessage(new ChatMessage("Copied coordinates to clipboard.", false, 0));
    }
}
