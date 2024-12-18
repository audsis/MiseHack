package org.minecraft.wise.impl.features.commands;

import org.minecraft.wise.api.command.Command;

public class Goat extends Command {

    public Goat() {
        super("Goat", "GOAT", new String[]{"goat"});
    }

    @Override
    public void run(String[] cmd) {
        System.out.println("owowowoowowowowoowowowowoowowowo comman workin!!!1!");
    }
}
