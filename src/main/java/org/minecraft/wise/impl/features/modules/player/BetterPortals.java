package org.minecraft.wise.impl.features.modules.player;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class BetterPortals extends Module {
    public static BetterPortals INSTANCE;
    public final Value<Boolean> chat = new ValueBuilder<Boolean>().withDescriptor("Chat").withValue(false).register(this);

    public BetterPortals() {
        super("BetterPortals", Category.Player);
        INSTANCE = this;
        setDescription("Controls certain quality of life features for Nether Portals");
    }
}
