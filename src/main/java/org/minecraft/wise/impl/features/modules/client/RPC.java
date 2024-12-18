package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.discord.DiscordPresence;
import org.minecraft.wise.api.feature.module.Module;

public class RPC extends Module {

    private final DiscordPresence presence = new DiscordPresence();

    public RPC() {
        super("RPC", Category.Client);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        presence.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        presence.stop();
    }
}
