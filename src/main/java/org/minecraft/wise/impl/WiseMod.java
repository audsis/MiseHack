package org.minecraft.wise.impl;

import org.minecraft.GitInfo;
import org.minecraft.wise.api.gui.notifications.NotificationProcessor;
import org.minecraft.wise.api.management.SavableManager;
import org.minecraft.wise.api.utils.render.shader.GL;
import org.minecraft.wise.api.utils.render.shader.PostProcessRenderer;
import org.minecraft.wise.api.utils.render.shader.post.PostProcessShaders;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class WiseMod {

    public static final String MOD_ID = "minecraft";
    public static final String VERSION = "1.0.0";
    public static final String NAME_UNICODE = "☄";
    public static String NAME = "Wisehack";
    public static final String NAME_VERSION = NAME + " " + GitInfo.VERSION + "+" + GitInfo.GIT_REVISION + "." + GitInfo.GIT_SHA.substring(0, 10);
    public static NotificationProcessor notificationProcessor;

    public void init() {
        Register.INSTANCE = new Register();
        Register.INSTANCE.registerAll();
        notificationProcessor = new NotificationProcessor();

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        GL.init();
    }

    public static void postInit() {
        SavableManager.INSTANCE.load();
        PostProcessShaders.init();
        PostProcessRenderer.init();
    }

    static class ShutdownHook extends Thread {
        ShutdownHook() {
        }

        @Override
        public void run() {
            super.run();
            try {
                SavableManager.INSTANCE.save();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static Identifier identifier(String path) {
        return Identifier.of(WiseMod.MOD_ID, path);
    }

    public static boolean isBaritonePresent() {
        return FabricLoader.getInstance().getModContainer("baritone").isPresent();
    }
}
