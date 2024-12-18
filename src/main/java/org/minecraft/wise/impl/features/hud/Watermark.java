package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import org.minecraft.wise.impl.features.modules.client.HudEditor;

public class Watermark extends HudComponent {


    public Watermark() {
        super("Watermark");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        FontManager.drawText(event.getContext(),
                HudEditor.INSTANCE.lowercase.getValue() ? WiseMod.NAME_VERSION.toLowerCase() : WiseMod.NAME_VERSION,
                xPos.getValue().intValue(),
                yPos.getValue().intValue(),
                HudColors.getTextColor(yPos.getValue().intValue()).getRGB());

        if (autoPos.getValue()) {
            xPos.setValue(2);
            yPos.setValue(2);
        }
    }
}
