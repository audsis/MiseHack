package org.minecraft.wise.api.gui.notifications;


import org.minecraft.wise.api.management.FontManager;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationProcessor {
    public static NotificationProcessor INSTANCE;
    public final AnimationNotification animationNotification;
    private final ArrayList<Notification> notifications = new ArrayList<>();

    public NotificationProcessor() {
        INSTANCE = this;
        animationNotification = new AnimationNotification();
    }

    public void handleNotifications(DrawContext context, int x, int posY) {
        for (int i2 = 0; i2 < this.getNotifications().size(); ++i2) {
            getNotifications().get(i2).onDraw(context, x, posY);
            posY += FontManager.getHeight("AAAAAA") + 1;
        }
    }

    public void addNotification(String text, long duration) {
        getNotifications().add(new Notification(text, duration));
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public class AnimationNotification extends Thread {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        public float offset;

        public AnimationNotification() {
            start();
        }

        @Override
        public void run() {
            for (int i2 = 0; i2 < NotificationProcessor.this.getNotifications().size(); ++i2) {
                try {
                    NotificationProcessor.this.getNotifications().get(i2).animation();
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public void start() {
            scheduler.scheduleAtFixedRate(this, 0L, 3L, TimeUnit.MILLISECONDS);
        }
    }
}