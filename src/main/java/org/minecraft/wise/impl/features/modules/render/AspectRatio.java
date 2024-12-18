package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class AspectRatio extends Module {
    public static AspectRatio INSTANCE;
    public final Value<Number> ratio = new ValueBuilder<Number>().withDescriptor("Ratio").withValue(1.5f).withRange(0.1f, 5.0f).register(this);

    public AspectRatio() {
        super("AspectRatio", Category.Render);
        INSTANCE = this;
        setDescription("Fortnite mode");
    }

}
