package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class AntiCheat extends Module {
    public static AntiCheat INSTANCE;
    public final Value<Boolean> rotate = new ValueBuilder<Boolean>().withDescriptor("Rotate").withValue(true).register(this);
    public final Value<Boolean> strictDirection = new ValueBuilder<Boolean>().withDescriptor("StrictDirection").withValue(true).register(this);
    public final Value<Boolean> moveFix = new ValueBuilder<Boolean>().withDescriptor("MoveFix").withValue(true).register(this);

    public AntiCheat() {
        super("AntiCheat", Category.Client);
        INSTANCE = this;
    }


}
