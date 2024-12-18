package org.minecraft.wise.impl.features.modules.client;

import baritone.api.BaritoneAPI;
import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class Baritone extends Module {

    private final Value<Boolean> place = new ValueBuilder<Boolean>().withDescriptor("Place").withValue(true).register(this);
    private final Value<Boolean> breakBlocks = new ValueBuilder<Boolean>().withDescriptor("Break").withValue(true).register(this);
    private final Value<Boolean> sprint = new ValueBuilder<Boolean>().withDescriptor("Sprint").withValue(true).register(this);
    private final Value<Boolean> useInventory = new ValueBuilder<Boolean>().withDescriptor("UseInventory").withValue(false).register(this);
    private final Value<Boolean> vines = new ValueBuilder<Boolean>().withDescriptor("Vines").withValue(true).register(this);
    private final Value<Boolean> jumpAtBuildHeight = new ValueBuilder<Boolean>().withDescriptor("JumpAtBuildHeight").withValue(false).register(this);
    private final Value<Boolean> waterBucketFall = new ValueBuilder<Boolean>().withDescriptor("WaterBucketFall").withValue(false).register(this);
    private final Value<Boolean> parkour = new ValueBuilder<Boolean>().withDescriptor("Parkour").withValue(true).register(this);
    private final Value<Boolean> parkourPlace = new ValueBuilder<Boolean>().withDescriptor("ParkourPlace").withValue(false).register(this);
    private final Value<Boolean> parkourAscend = new ValueBuilder<Boolean>().withDescriptor("ParkourAscend").withValue(true).register(this);
    private final Value<Boolean> diagonalAscend = new ValueBuilder<Boolean>().withDescriptor("DiagonalAscend").withValue(false).register(this);
    private final Value<Boolean> diagonalDescend = new ValueBuilder<Boolean>().withDescriptor("DiagonalDescend").withValue(false).register(this);
    private final Value<Boolean> mineDownward = new ValueBuilder<Boolean>().withDescriptor("MineDownward").withValue(true).register(this);
    private final Value<Boolean> legitMine = new ValueBuilder<Boolean>().withDescriptor("LegitMine").withValue(false).register(this);
    private final Value<Boolean> logOnArrival = new ValueBuilder<Boolean>().withDescriptor("LogOnArrival").withValue(false).register(this);
    private final Value<Boolean> freeLook = new ValueBuilder<Boolean>().withDescriptor("FreeLook").withValue(true).register(this);
    private final Value<Boolean> antiCheat = new ValueBuilder<Boolean>().withDescriptor("AntiCheat").withValue(false).register(this);
    private final Value<Boolean> strictLiquid = new ValueBuilder<Boolean>().withDescriptor("StrictLiquid").withValue(false).register(this);
    private final Value<Boolean> censorCoords = new ValueBuilder<Boolean>().withDescriptor("CensorCoords").withValue(false).register(this);
    private final Value<Boolean> censorCommands = new ValueBuilder<Boolean>().withDescriptor("CensorCommands").withValue(false).register(this);
    private final Value<Boolean> debug = new ValueBuilder<Boolean>().withDescriptor("Debug").withValue(false).register(this);

    public Baritone() {
        super("Baritone", Category.Client);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        BaritoneAPI.getSettings().allowPlace.value = place.getValue();
        BaritoneAPI.getSettings().allowBreak.value = breakBlocks.getValue();
        BaritoneAPI.getSettings().allowSprint.value = sprint.getValue();
        BaritoneAPI.getSettings().allowInventory.value = useInventory.getValue();
        BaritoneAPI.getSettings().allowVines.value = vines.getValue();
        BaritoneAPI.getSettings().allowJumpAt256.value = jumpAtBuildHeight.getValue();
        BaritoneAPI.getSettings().allowWaterBucketFall.value = waterBucketFall.getValue();
        BaritoneAPI.getSettings().allowParkour.value = parkour.getValue();
        BaritoneAPI.getSettings().allowParkourAscend.value = parkourAscend.getValue();
        BaritoneAPI.getSettings().allowParkourPlace.value = parkourPlace.getValue();
        BaritoneAPI.getSettings().allowDiagonalAscend.value = diagonalAscend.getValue();
        BaritoneAPI.getSettings().allowDiagonalDescend.value = diagonalDescend.getValue();
        BaritoneAPI.getSettings().allowDownward.value = mineDownward.getValue();
        BaritoneAPI.getSettings().legitMine.value = legitMine.getValue();
        BaritoneAPI.getSettings().disconnectOnArrival.value = logOnArrival.getValue();
        BaritoneAPI.getSettings().freeLook.value = freeLook.getValue();
        BaritoneAPI.getSettings().antiCheatCompatibility.value = antiCheat.getValue();
        BaritoneAPI.getSettings().strictLiquidCheck.value = strictLiquid.getValue();
        BaritoneAPI.getSettings().censorCoordinates.value = censorCoords.getValue();
        BaritoneAPI.getSettings().censorRanCommands.value = censorCommands.getValue();
        BaritoneAPI.getSettings().chatDebug.value = debug.getValue();
    }
}
