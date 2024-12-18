package org.minecraft.wise.api.macro;

import org.minecraft.wise.api.wrapper.IMinecraft;

public class Macro implements IMinecraft {
    int key;
    String value;

    public Macro(int k, String v) {
        key = k;
        value = v;
    }

    public void onMacro() {
        if (mc.player != null)
            mc.player.networkHandler.sendChatMessage(value);
    }

    public int getKey(){
        return key;
    }

    public String getValue(){
        return value;
    }
}