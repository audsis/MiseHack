package org.minecraft.wise.mixin.mixins.access;

import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public interface ILivingEntity {
    @Accessor("lastAttackedTicks")
    int getLastAttackedTicks();
//
//    @Access
    @Accessor("jumpingCooldown")
    void setLastJumpCooldown(int cooldown);

    @Accessor("jumping")
    boolean isJumping();

    @Accessor("HEALTH")
    static TrackedData<Float> getHealthId() {
        throw new IllegalStateException("HEALTH accessor has not been mixed in. Report this!");
    }
}
