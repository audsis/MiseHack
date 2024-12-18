package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class CustomSky extends Module {

    public static CustomSky INSTANCE;
    public final Value<Boolean> skyColor = new ValueBuilder<Boolean>().withDescriptor("SkyColor").withValue(true).register(this);
    public final Value<Color> sky = new ValueBuilder<Color>().withDescriptor("Sky").withValue(new Color(89, 89, 89)).register(this);
    public final Value<Boolean> fogColor = new ValueBuilder<Boolean>().withDescriptor("FogColor").withValue(false).register(this);
    public final Value<Color> fog = new ValueBuilder<Color>().withDescriptor("Fog").withValue(new Color(133, 205, 253)).register(this);
    public final Value<Boolean> cloudColor = new ValueBuilder<Boolean>().withDescriptor("CloudColor").withValue(false).register(this);
    public final Value<Color> clouds = new ValueBuilder<Color>().withDescriptor("Clouds").withValue(new Color(0, 150, 255)).register(this);

    public CustomSky() {
        super("CustomSky", Category.Render);
        INSTANCE = this;
    }
}
