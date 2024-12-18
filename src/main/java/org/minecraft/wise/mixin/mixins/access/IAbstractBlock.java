package org.minecraft.wise.mixin.mixins.access;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface IAbstractBlock {

    @Accessor("slipperiness")
    @Mutable
    void setSlipperiness(float slipperiness);
}
