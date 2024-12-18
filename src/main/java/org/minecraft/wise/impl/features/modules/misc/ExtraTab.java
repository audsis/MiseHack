package org.minecraft.wise.impl.features.modules.misc;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class ExtraTab extends Module {
    public static ExtraTab INSTANCE;
    public final Value<Number> size = new ValueBuilder<Number>().withDescriptor("Size").withValue(200).withRange(80, 1000).register(this);
    public final Value<Color> friendColor = new ValueBuilder<Color>().withDescriptor("Friend Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Color> selfColor = new ValueBuilder<Color>().withDescriptor("Self Color").withValue(new Color(255, 255, 255)).register(this);

    public ExtraTab() {
        super("ExtraTab", Category.Misc);
        INSTANCE = this;
    }
}
