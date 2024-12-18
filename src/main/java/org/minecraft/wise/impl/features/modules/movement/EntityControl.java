package org.minecraft.wise.impl.features.modules.movement;

import org.minecraft.wise.api.feature.module.Module;

public class EntityControl extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        super("EntityControl", Category.Movement);
        INSTANCE = this;
        setDescription("Lets you control ridable entities without a saddle.");
    }
}
