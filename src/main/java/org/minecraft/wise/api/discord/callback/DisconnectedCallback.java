package org.minecraft.wise.api.discord.callback;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {
    void apply(int p0, String p1);
}