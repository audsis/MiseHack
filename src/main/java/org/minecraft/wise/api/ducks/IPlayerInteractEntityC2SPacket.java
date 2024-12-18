package org.minecraft.wise.api.ducks;

import org.minecraft.wise.api.utils.entity.InteractType;
import net.minecraft.entity.Entity;

public interface IPlayerInteractEntityC2SPacket {

    Entity getEntity();

    InteractType getType();
}