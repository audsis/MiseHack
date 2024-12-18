package org.minecraft.wise.api.discord.callback;

import com.sun.jna.Callback;

public interface JoinGameCallback extends Callback {
    void apply(String p0);
}