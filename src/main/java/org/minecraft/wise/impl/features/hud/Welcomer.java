package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.Formatting;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;

import java.time.ZonedDateTime;

public class Welcomer extends HudComponent {

    private final Value<String> modes = new ValueBuilder<String>().withDescriptor("Modes").withValue("Normal").withModes("Normal", "Wise", "Extra", "Muslim", "Time").register(this);
    private final ZonedDateTime time = ZonedDateTime.now();

    public Welcomer() {
        super("Welcomer");
    }

    @Subscribe
    public void draw(Render2dEvent event) {

        String text = getText();
        int x = xPos.getValue().intValue();
        int y = yPos.getValue().intValue();

        if (autoPos.getValue()) {
            x = event.getContext().getScaledWindowWidth() - (FontManager.getWidth(text) / 2);
            y = 2;
        }

        FontManager.drawText(event.getContext(), text, x, y, HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
    }

    public String getText() {
        String timer = time.getHour() <= 11 ? "Good Morning " : time.getHour() <= 18 ? "Good Afternoon " : "Good Evening ";
        switch (modes.getValue()) {
            case "Wise" -> {
                return "Welcome to Wisehack, " + Formatting.WHITE + mc.player.getName().getString();
            }
            case "Extra" -> {
                return "Hello, " + Formatting.WHITE + mc.player.getName().getString() + Formatting.RESET + " :^)";
            }
            case "Muslim" -> {
                return "Salam Alaikum, " + Formatting.WHITE + mc.player.getName().getString() + Formatting.RESET + " :^)";
            }
            case "Time" -> {
                return timer + Formatting.WHITE + mc.player.getName().getString();
            }
        }
        return "Hello, " + Formatting.WHITE +  mc.player.getName().getString();
    }
}
