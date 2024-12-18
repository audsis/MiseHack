package org.minecraft.wise.api.event;

import org.minecraft.wise.api.event.bus.Event;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BreakEvent extends Event {
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;

    public BreakEvent(BlockPos pos, BlockState state, Direction direction) {
        this.pos = pos;
        this.state = state;
        this.direction = direction;
    }

    public Direction getDir() {
        return direction;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }
}