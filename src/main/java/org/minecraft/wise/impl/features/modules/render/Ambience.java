package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class Ambience extends Module {
    public static boolean rendering = true;
    public static Ambience INSTANCE;
    public final Value<Color> color = new ValueBuilder<Color>().withDescriptor("Color").withValue(new Color(0, 0, 255)).register(this);

    public Ambience() {
        super("Ambience", Category.Render);
        INSTANCE = this;
    }
}
