package org.minecraft.wise.impl.features.modules.player;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class PlayerTweaks extends Module {

    public static PlayerTweaks INSTANCE;
    public Value<Boolean> noJumpDelay = new ValueBuilder<Boolean>().withDescriptor("NoJumpDelay").withValue(true).register(this);
    public Value<Boolean> antiSwim = new ValueBuilder<Boolean>().withDescriptor("AntiSwim").withValue(true).register(this);

    public PlayerTweaks() {
        super("PlayerTweaks", Category.Player);
        INSTANCE = this;
    }
}
