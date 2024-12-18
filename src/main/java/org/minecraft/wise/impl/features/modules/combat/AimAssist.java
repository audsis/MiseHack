package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;

public class AimAssist extends Module {

    public AimAssist() {
        super("AimAssist", Category.Combat);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;
    }

    /**
     * uhhhhhh why am i setitng the players yaw
     * instead i should use the robot class to access mouse x and y,
     * but i may need to detect a color change for enemies which is supppeerrr annoying
     * will do that in the future or if i find a new method.....
     */
    public void getAiming(float yaw, float pitch) {
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
