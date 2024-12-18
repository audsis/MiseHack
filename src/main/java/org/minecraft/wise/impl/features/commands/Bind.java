package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FeatureManager;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;

public class Bind extends Command {

    public Bind() {
        super("Bind", "binds a module", new String[]{"bind", "b"});
    }

    @Override
    public void run(String[] args) {
        if (args.length > 2) {
            for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
                Module module;
                String modName;
                if (!(feature instanceof Module) || !(module = (Module) feature).getName().replace(" ", "").equalsIgnoreCase(args[1]))
                    continue;
                module.getBind().getString(args[2]);
                ChatUtils.sendMessage(new ChatMessage("Bound " + module.getName() + " to " + module.getBind().getKey(), false, 0));
            }
        } else {
            ChatUtils.sendMessage(new ChatMessage("Please input a valid command", false, 0));
        }
    }
}
