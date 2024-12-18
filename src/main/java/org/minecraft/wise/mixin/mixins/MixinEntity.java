package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.EntityMovementEvent;
import org.minecraft.wise.api.event.VelocityEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.player.PlayerTweaks;
import org.minecraft.wise.impl.features.modules.player.Velocity;
import org.minecraft.wise.impl.features.modules.render.NoRender;
import org.minecraft.wise.api.ducks.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMinecraft, IEntity {

    @Shadow
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Shadow
    private int id;

    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    public void moveEntityHookPre(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (mc.player == null)
            return;

        if (this.id == mc.player.getId()) {
            EntityMovementEvent event = new EntityMovementEvent(movementType, movement.x, movement.y, movement.z, movement.horizontalLength());
            Bus.EVENT_BUS.post(event);

            if (event.isCancelled())
                ci.cancel();
        }
    }

    @ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void pushAwayFromHook(Args args) {
        if (Velocity.INSTANCE.isEnabled() && Velocity.INSTANCE.players.getValue()) {
            args.set(0, (Object)0.0);
            args.set(1, (Object)0.0);
            args.set(2, (Object)0.0);
        }
    }

    @Inject(method = "isOnFire", at = @At(value="HEAD"), cancellable=true)
    public void isOnFireHook(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.fireOverlay.getValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateVelocity", at = @At(value = "HEAD"), cancellable = true)
    private void hookVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        if ((Object) this == mc.player) {
            VelocityEvent event = new VelocityEvent(movementInput, speed, mc.player.getYaw(), movementInputToVelocity(movementInput, speed, mc.player.getYaw()));
            Bus.EVENT_BUS.post(event);

            if (event.isCancelled()) {
                ci.cancel();
                mc.player.setVelocity(mc.player.getVelocity().add(event.getVelocity()));
            }
        }
    }

    @Inject(method = "setPose", at = @At(value = "HEAD"), cancellable = true)
    public void onSetPose(EntityPose pose, CallbackInfo ci) {
        if (PlayerTweaks.INSTANCE.antiSwim.getValue() && (pose == EntityPose.SWIMMING) && mc.player.isTouchingWater()) {
            ci.cancel();
        }
    }
}
