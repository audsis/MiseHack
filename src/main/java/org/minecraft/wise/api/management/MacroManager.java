package org.minecraft.wise.api.management;

import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.macro.Macro;

import java.util.ArrayList;
import java.util.List;

public class MacroManager {
    public static MacroManager INSTANCE;
    List<Macro> macros;

    public MacroManager() {
        macros = new ArrayList<>();
        Bus.EVENT_BUS.register(this);
        System.out.println("Registering Macros.");
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public Macro getMacroByValue(String v) {
        return getMacros().stream().filter(mm -> mm.getValue().equals(v)).findFirst().orElse(null);
    }

    public Macro getMacroByKey(int key) {
        return getMacros().stream().filter(mm -> mm.getKey() == key).findFirst().orElse(null);
    }

    public void addMacro(Macro m) {
        macros.add(m);
    }

    public void delMacro(Macro m) {
        macros.remove(m);
    }
}