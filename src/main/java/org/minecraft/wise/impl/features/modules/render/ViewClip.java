package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class ViewClip extends Module {
    public static ViewClip INSTANCE;
    public final Value<Number> distance = new ValueBuilder<Number>().withDescriptor("Distance").withValue(6).withRange(1, 20).register(this);

    public ViewClip() {
        super("ViewClip", Category.Render);
        INSTANCE = this;
        setDescription("Lets you see through walls in F5");
    }
}
