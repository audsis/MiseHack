package org.minecraft.wise.api.discord.callback;

import com.sun.jna.Callback;
import org.minecraft.wise.api.discord.DiscordUser;

public interface ReadyCallback extends Callback {
    void apply(DiscordUser p0);
}