package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.Timer;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.BlockPos;

public class PortalGodMode extends Module {

    private Timer timer = new Timer.Single();

    public PortalGodMode() {
        super("PortalGodMode", Category.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (int x = (int) (mc.player.getX() - 2.0); x < mc.player.getX() + 2.0; ++x) {
            for (int z = (int) (mc.player.getZ() - 2.0); z < mc.player.getZ() + 2.0; ++z) {
                for (int y = (int) (mc.player.getY() - 2.0); y < mc.player.getY() + 2.0; ++y) {
                    if (mc.world.getBlockState(BlockPos.ofFloored(x, y, z)).getBlock() == Blocks.NETHER_PORTAL) {
                        timer.reset();
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof TeleportConfirmC2SPacket && timer.hasPassed(5000)) {
            event.cancel();
        }
    }
}
