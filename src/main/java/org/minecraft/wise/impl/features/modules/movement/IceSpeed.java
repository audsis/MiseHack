package org.minecraft.wise.impl.features.modules.movement;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.mixin.mixins.access.IAbstractBlock;
import net.minecraft.block.Blocks;

public class IceSpeed extends Module {

    public IceSpeed() {
        super("IceSpeed", Category.Movement);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ((IAbstractBlock) Blocks.ICE).setSlipperiness(0.4f);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        ((IAbstractBlock) Blocks.ICE).setSlipperiness(0.98f);
    }
}
