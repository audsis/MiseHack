package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;

public class FullBright extends Module {

    public static FullBright INSTANCE;

    public FullBright() {
        super("FullBright", Category.Render);
        INSTANCE = this;
        setDescription("Prevents you from running into walls in caves or dark areas.");
    }
}
