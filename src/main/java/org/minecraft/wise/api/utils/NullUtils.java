package org.minecraft.wise.api.utils;

import org.minecraft.wise.api.wrapper.IMinecraft;

public class NullUtils implements IMinecraft {

    public static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }
}
