package org.minecraft.wise.api.command;

import org.minecraft.wise.api.event.bus.Bus;

public abstract class Command {
    final String name;
    final String desc;
    final String[] alias;

    public Command(String name, String desc, String[] alias) {
        this.name = name;
        this.desc = desc;
        this.alias = alias;
        Bus.EVENT_BUS.register(this);
    }

    public abstract void run(String[] cmd);

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public String[] getAlias() {
        return this.alias;
    }
}