package org.minecraft.wise.api.discord;

import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.WiseMod;

public class DiscordPresence implements IMinecraft {
    private static final DiscordRichPresence presence = new DiscordRichPresence();
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private static Thread thread;

    public synchronized void start() {
        if (thread != null)
            thread.interrupt();

        DiscordEventHandlers handlers = new DiscordEventHandlers();

        rpc.Discord_Initialize("1299731409983901799", handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details = getDetails();

        presence.largeImageKey = "rpc";
        presence.largeImageText = "Version: " + WiseMod.VERSION;

        rpc.Discord_UpdatePresence(presence);

        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }

                rpc.Discord_RunCallbacks();

                presence.details = getDetails();
                assert mc.player != null;
                presence.state = mc.player.getName().getString();

                rpc.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler");

        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
            thread = null;
        }

        rpc.Discord_Shutdown();
    }

    private String getDetails() {
        String fire = mc.player == null ? "Staring at the menu" : mc.isIntegratedServerRunning() ? "Playing alone" : "Playing multiplayer";
        return fire;
    }
}