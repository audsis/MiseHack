package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class ExtraPlace extends Module {

    public ExtraPlace() {
        super("ExtraPlace", Category.Player);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!(mc.crosshairTarget instanceof BlockHitResult result)) return;

        BlockPos blockPos = result.getBlockPos();

        if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem))
            return;

        if (!(mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR ||
                mc.world.getBlockState(blockPos).getBlock() == Blocks.LAVA ||
                mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER)) return;

        if (mc.options.useKey.isPressed()) {
            ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);

            if (actionResult.isAccepted() && actionResult.shouldSwingHand())
                mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
