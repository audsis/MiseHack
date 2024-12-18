package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

public class Parkour extends Module {

    public Parkour() {
        super("Parkour", Category.Movement);
    }

    @Subscribe
    public void onUpdate(TickEvent event) {
        if (NullUtils.nullCheck()) return;

        if (!mc.player.isOnGround() || mc.options.jumpKey.isPressed()) return;

        if (mc.player.isSneaking() || mc.options.sneakKey.isPressed()) return;

        Box entityBoundingBox = mc.player.getBoundingBox();
        Box offsetBox = entityBoundingBox.offset(0, -0.5, 0).expand(-0.001, 0, -0.001);
        List<VoxelShape> collisionBoxes = mc.world.getEntityCollisions(mc.player, offsetBox);

        if (collisionBoxes.isEmpty()) {
            mc.player.jump();
        }
    }
}
