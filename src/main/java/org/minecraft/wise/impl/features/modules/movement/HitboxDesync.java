package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.util.math.Vec3d;

public class HitboxDesync extends Module {

    public HitboxDesync() {
        super("HitboxDesync", Category.Movement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck()) {
            setEnabled(false);
            return;
        }

        Vec3d vec3d = mc.player.getBlockPos().toCenterPos();

        boolean flagX = (vec3d.x - mc.player.getX()) > 0;
        boolean flagZ = (vec3d.z - mc.player.getZ()) > 0;

        double x = vec3d.x + 0.20000000009497754 * (flagX ? -1 : 1);
        double z = vec3d.z + 0.2000000000949811 * (flagZ ? -1 : 1);

        mc.player.setPosition(x, mc.player.getY(), z);

        setEnabled(false);
    }
}
