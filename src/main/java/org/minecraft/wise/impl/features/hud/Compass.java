package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class Compass extends HudComponent {

    final Value<Number> scale = new ValueBuilder<Number>().withDescriptor("Scale").withValue(3.0D).withRange(1.D, 6.0D).register(this);

    private enum Direction {
        N,
        W,
        S,
        E
    }

    private static final double HALF_PI = Math.PI / 2;

    public Compass() {
        super("Compass");
    }

    @Subscribe
    public void draw(Render2dEvent event) {

        for (Direction dir : Direction.values()) {
            double rad = getPosOnCompass(dir);
            FontManager.drawText(event.getContext(),
                    dir.name(),
                    (int) (xPos.getValue().floatValue() + getX(rad)),
                    (int) (yPos.getValue().floatValue() + getY(rad)),
                    dir == Direction.N ? HudColors.getTextColor(yPos.getValue().intValue()).getRGB() : Color.WHITE.getRGB());

        }

    }

    private double getX(double rad) {
        return Math.sin(rad) * (scale.getValue().floatValue() * 10);
    }

    private double getY(double rad) {
        final double epicPitch = MathHelper.clamp(mc.player.getPitch() + 30f, -90f, 90f);
        final double pitchRadians = Math.toRadians(epicPitch);
        return Math.cos(rad) * Math.sin(pitchRadians) * (scale.getValue().floatValue() * 10);
    }

    private static double getPosOnCompass(Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.player.getYaw()));
        int index = dir.ordinal();
        return yaw + (index * HALF_PI);
    }
}