package org.minecraft.wise.impl.features.hud;

import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.gui.hudeditor.HudGui;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import org.minecraft.wise.impl.features.modules.client.HudEditor;
import net.minecraft.util.Formatting;

public class NotificationsHud extends HudComponent {

    public NotificationsHud() {
        super("Notifications");
    }

    @Override
    public void draw(Render2dEvent event) {
        if (isEnabled()) {
            if (NullUtils.nullCheck())
                return;

            if (mc.currentScreen instanceof HudGui && HudEditor.INSTANCE.isEnabled()) {
                FontManager.drawText(event.getContext(),
                        Formatting.GRAY + "{" + Formatting.YELLOW +
                                "!" + Formatting.GRAY +
                                "} " + Formatting.WHITE +
                                "This is a " + Formatting.RESET + "notification.",
                        xPos.getValue().intValue(),
                        yPos.getValue().intValue(),
                        HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
                return;
            }

            WiseMod.notificationProcessor.handleNotifications(event.getContext(),
                    xPos.getValue().intValue(),
                    yPos.getValue().intValue());

            width = FontManager.getWidth("{!} This is a notification.");
        }
    }
}
