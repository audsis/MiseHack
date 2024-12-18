package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FeatureManager;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;

public class Enable extends Command {

    public Enable() {
        super("Enable", "enables a module", new String[]{"enable", "b"});
    }

    @Override
    public void run(String[] args) {
        if (args.length > 2) {
            for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
                Module module;
                String modName;
                if (!(feature instanceof Module) || !(module = (Module) feature).getName().replace(" ", "").equalsIgnoreCase(args[1]))
                    continue;
                ChatUtils.sendMessage(new ChatMessage(module.isEnabled() ? "Disabled " + module.getName() : "Enabled " + module.getName(), false, 0));
                module.setEnabled(Boolean.parseBoolean(args[2]));
            }
            for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
                HudComponent module;
                String modName;
                if (!(feature instanceof HudComponent) || !(module = (HudComponent) feature).getName().replace(" ", "").equalsIgnoreCase(args[1]))
                    continue;
                ChatUtils.sendMessage(new ChatMessage(module.isEnabled() ? "Disabled " + module.getName() : "Enabled " + module.getName(), false, 0));
                module.setEnabled(Boolean.parseBoolean(args[2]));
            }
        } else {
            ChatUtils.sendMessage(new ChatMessage("Please input a valid command", false, 0));
        }
    }
}