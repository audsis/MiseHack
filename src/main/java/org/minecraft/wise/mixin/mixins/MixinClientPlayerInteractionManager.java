package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.BreakEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager implements IMinecraft {

    @Inject(method = "attackBlock", at = @At(value = "HEAD"))
    private void sendAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = mc.world.getBlockState(pos);
        BreakEvent event = new BreakEvent(pos, state, direction);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            cir.cancel();
        }
    }
}
