package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class FontMod extends Module {

    public static FontMod INSTANCE;
    public final Value<Number> fontSize = new ValueBuilder<Number>().withDescriptor("Font Size").withValue(18).withRange(10, 25).register(this);
    public final Value<Boolean> italic = new ValueBuilder<Boolean>().withDescriptor("Italic").withValue(false).register(this);
    public final Value<Boolean> shadow = new ValueBuilder<Boolean>().withDescriptor("Shadow").withValue(true).register(this);
    public final Value<Boolean> chat = new ValueBuilder<Boolean>().withDescriptor("Chat").withValue(false).register(this);

    public FontMod() {
        super("FontMod", Category.Client);
        INSTANCE = this;
    }
}
