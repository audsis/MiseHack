package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;
import org.minecraft.wise.api.macro.Macro;
import org.minecraft.wise.api.management.MacroManager;

public class Macros extends Command {

    public Macros() {
        super("Macro", "Adds a chat macro", new String[]{"macro", "macros"});
    }

    @Override
    public void run(String[] cmd) {
        if (cmd.length > 3) {
            if (cmd[1].equalsIgnoreCase("add")) {
                MacroManager.INSTANCE.addMacro(new Macro(Integer.parseInt(cmd[2]), cmd[3]));
            } else if (cmd[1].equalsIgnoreCase("del")) {
                MacroManager.INSTANCE.delMacro(new Macro(Integer.parseInt(cmd[2]), cmd[3]));
            }
        }

    }
}
