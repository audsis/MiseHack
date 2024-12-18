package org.minecraft.wise.api.gui.notifications;

import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class Notification {
    private final String text;
    private final Timer timer = new Timer.Single();
    private float textOffset;
    private final long disableTime;

    public Notification(String text, long disableTime) {
        this.text = text;
        this.disableTime = disableTime;
        this.textOffset = -FontManager.getWidth(text);
        timer.reset();
    }

    public void onDraw(DrawContext context, int x, int offset) {
        if (!(textOffset >= (float) Math.negateExact(FontManager.getWidth(text)))) {
            WiseMod.notificationProcessor.getNotifications().remove(this);
        }
        FontManager.drawText(context, Formatting.GRAY + "{" + Formatting.YELLOW + "!" + Formatting.GRAY + "} " + Formatting.WHITE + text, (int) (x + textOffset), offset, HudColors.getTextColor(offset).getRGB());
    }

    public void animation() {
        float targetOffset = (float) -FontManager.getWidth(text);

        if (!timer.hasPassed(disableTime)) {
            if (textOffset < 0.0f) {
                textOffset += Math.max(0.5f, Math.abs(textOffset) * 0.05f);
            }
        } else {
            if (textOffset > targetOffset) {
                textOffset -= Math.max(0.5f, Math.abs(textOffset - targetOffset) * 0.05f);
            }
        }
    }
}