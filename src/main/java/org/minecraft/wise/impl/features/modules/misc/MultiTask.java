package org.minecraft.wise.impl.features.modules.misc;

import org.minecraft.wise.api.feature.module.Module;

public class MultiTask extends Module {

    public static MultiTask INSTANCE;

    public MultiTask() {
        super("MultiTask", Category.Misc);
        INSTANCE = this;
    }
}
