package org.minecraft.wise.api.value.custom;

import org.minecraft.wise.api.utils.Keyboard;

public class Bind {
    int key = -1;

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void getString(String string) {
        String s = string.toUpperCase();
        if (s.equals("NONE") || s.equals("NULL"))
            key = -1;

        key = Keyboard.getKeyIndex(s);
    }
}