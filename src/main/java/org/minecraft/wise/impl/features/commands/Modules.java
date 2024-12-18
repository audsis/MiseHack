package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.management.FeatureManager;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import net.minecraft.util.Formatting;

public class Modules extends Command {
    public Modules() {
        super("Modules", "Modules available", new String[]{"modules", "h"});
    }

    @Override
    public void run(String[] cmd) {
        StringBuilder modulesList = new StringBuilder("\nModules: ");

        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if (feature.isEnabled()) {
                modulesList.append(Formatting.GREEN);
            } else {
                modulesList.append(Formatting.RED);
            }
            modulesList.append(feature.getName()).append(", ");
        }


        if (modulesList.length() > 9) {
            modulesList.setLength(modulesList.length() - 2);
        }

        ChatUtils.sendMessage(new ChatMessage(modulesList.toString(), false, 0));
    }
}
