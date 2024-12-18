package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class ParticleEditor extends Module {

    public static ParticleEditor INSTANCE;
    public final Value<Number> scale = new ValueBuilder<Number>().withDescriptor("Scale").withValue(1.0).withRange(0.0, 3.0).register(this);
    public final Value<Boolean> totems = new ValueBuilder<Boolean>().withDescriptor("Totems").withValue(true).register(this);
    public final Value<Color> totemColor1 = new ValueBuilder<Color>().withDescriptor("TotemColor1").withValue(new Color(9, 9, 255)).register(this);
    public final Value<Color> totemColor2 = new ValueBuilder<Color>().withDescriptor("TotemColor2").withValue(new Color(255, 9, 9)).register(this);
    public final Value<Boolean> rockets = new ValueBuilder<Boolean>().withDescriptor("Rockets").withValue(true).register(this);
    public final Value<Color> rocketColor1 = new ValueBuilder<Color>().withDescriptor("RocketColor1").withValue(new Color(9, 9, 255)).register(this);
    public final Value<Color> rocketColor2 = new ValueBuilder<Color>().withDescriptor("RocketColor2").withValue(new Color(255, 9, 9)).register(this);

    public ParticleEditor() {
        super("ParticleEditor", Category.Render);
        INSTANCE = this;
    }
}
