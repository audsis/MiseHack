package org.minecraft.wise.impl.features.modules.client;

import net.minecraft.util.math.MathHelper;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class HudColors extends Module {
    public static HudColors INSTANCE;
    public final Value<String> colorMode = new ValueBuilder<String>().withDescriptor("Color Mode").withValue("Two Step Color").withModes("Static", "Rainbow", "Two Step Color", "Three Step Color").register(this);
    final Value<Number> stepLength = new ValueBuilder<Number>().withDescriptor("Step Length").withValue(30).withRange(10, 130).register(this);
    public final Value<Number> stepSpeed = new ValueBuilder<Number>().withDescriptor("Step Speed").withValue(30).withRange(1, 130).register(this);
    public final Value<Color> mainColor = new ValueBuilder<Color>().withDescriptor("Main Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Color> stepColor = new ValueBuilder<Color>().withDescriptor("Step Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Color> endColor = new ValueBuilder<Color>().withDescriptor("End Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Number> Saturation = new ValueBuilder<Number>().withDescriptor("Saturation").withValue(108).withRange(1, 255).register(this);
    public final Value<Number> Brightness = new ValueBuilder<Number>().withDescriptor("Brightness").withValue(255).withRange(1, 255).register(this);

    public HudColors() {
        super("HudColors", Feature.Category.Client);
        INSTANCE = this;
        setDescription("Manages HUD Colors.");
    }

    public static Color getTextColor(int y2) {
        double roundY = Math.sin(Math.toRadians(((long) y2 * HudColors.INSTANCE.stepLength.getValue().intValue()) + (double) System.currentTimeMillis() / (long) HudColors.INSTANCE.stepSpeed.getValue().intValue()));
        roundY = Math.abs(roundY);

        return switch (HudColors.INSTANCE.colorMode.getValue()) {
            case "Two Step Color" ->
                    ColorUtil.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), HudColors.INSTANCE.mainColor.getValue(), HudColors.INSTANCE.stepColor.getValue());
            case "Three Step Color" ->
                    ColorUtil.interpolate((float) MathHelper.clamp(roundY, 0.0, 1.0), HudColors.INSTANCE.mainColor.getValue(), HudColors.INSTANCE.stepColor.getValue(), HudColors.INSTANCE.endColor.getValue());
            case "Rainbow" ->
                    ColorUtil.rainbow(y2 * (HudColors.INSTANCE.stepLength.getValue().intValue() +
                                                  HudColors.INSTANCE.stepSpeed.getValue().intValue()) * 2,
                            HudColors.INSTANCE.Saturation.getValue().intValue(),
                            HudColors.INSTANCE.Brightness.getValue().intValue());
            default -> HudColors.INSTANCE.mainColor.getValue();
        };
    }
}
