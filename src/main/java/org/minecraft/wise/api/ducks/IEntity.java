package org.minecraft.wise.api.ducks;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IEntity {
    @Mutable
    @Accessor(value="pos")
    void setPos(Vec3d var1);

    @Mutable
    @Accessor(value="blockPos")
    void setBlockPos(BlockPos var1);

    @Invoker("setFlag")
    void setFlag(int index, boolean value);
}

