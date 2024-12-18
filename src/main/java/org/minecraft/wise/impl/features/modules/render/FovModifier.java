package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class FovModifier extends Module {

    public static FovModifier INSTANCE;
    public final Value<Boolean> staticaf = new ValueBuilder<Boolean>().withDescriptor("Static").withValue(false).register(this);
    public final Value<Number> sprinting = new ValueBuilder<Number>().withDescriptor("Sprinting").withValue(1.0f).withRange(0.0, 2.5).register(this);
    public final Value<Number> swiftness = new ValueBuilder<Number>().withDescriptor("Swiftness").withValue(1.0f).withRange(0.0, 2.5).register(this);
    public final Value<Number> slowness = new ValueBuilder<Number>().withDescriptor("Slowness").withValue(1.0f).withRange(0.0, 2.5).register(this);
    public final Value<Number> aim = new ValueBuilder<Number>().withDescriptor("Aiming").withValue(1.0f).withRange(0.0, 2.5).register(this);
    public final Value<Number> spyglass = new ValueBuilder<Number>().withDescriptor("Spyglass").withValue(0.1f).withRange(0.0, 2.5).register(this);
    public final Value<Number> flying = new ValueBuilder<Number>().withDescriptor("Flying").withValue(1.0f).withRange(0.0, 2.5).register(this);

    public FovModifier() {
        super("FovModifier", Category.Render);
        INSTANCE = this;
    }
}
