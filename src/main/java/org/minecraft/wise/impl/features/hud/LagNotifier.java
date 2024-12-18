package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.Timer;

import java.awt.*;

public class LagNotifier extends HudComponent {

    private final Timer timer = new Timer.Single();

    public LagNotifier() {
        super("LagNotifier");
    }

    @Subscribe
    public void onPacketReceive(PacketEvent event) {
        if (event.getTime() == PacketEvent.Time.Receive) {
            timer.reset();
        }
    }

    @Subscribe
    public void draw(Render2dEvent event) {

        if (timer.hasPassed(1500)) {
            FontManager.drawText(event.getContext(), "Server has not responded in",
                    xPos.getValue().intValue(),
                    yPos.getValue().intValue(),
                    new Color(120, 120, 120).getRGB());
        }
    }
}
