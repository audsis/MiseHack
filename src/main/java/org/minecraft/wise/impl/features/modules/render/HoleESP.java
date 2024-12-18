package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.world.HoleUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoleESP extends Module {

    private final Value<Number> lineWidth = new ValueBuilder<Number>().withDescriptor("Line Width").withValue(2.0f).withRange(0.1, 5).register(this);
    private final Value<Number> height = new ValueBuilder<Number>().withDescriptor("Height").withValue(1).withRange(0, 2).register(this);
    private final Value<Number> range = new ValueBuilder<Number>().withDescriptor("Range").withValue(5.0).withRange(1.0, 30.0).register(this);
    private final Value<Boolean> doubles = new ValueBuilder<Boolean>().withDescriptor("Doubles").withValue(true).register(this);
    private final Value<Color> bedrockFill = new ValueBuilder<Color>().withDescriptor("Bedrock Fill").withValue(new Color(0, 255, 0, 100)).register(this);
    private final Value<Color> bedrockLine = new ValueBuilder<Color>().withDescriptor("Bedrock Line").withValue(new Color(255, 255, 255, 255)).register(this);
    private final Value<Color> obbyFill = new ValueBuilder<Color>().withDescriptor("Obby Fill").withValue(new Color(0, 255, 218, 100)).register(this);
    private final Value<Color> obbyLine = new ValueBuilder<Color>().withDescriptor("Obby Line").withValue(new Color(255, 255, 255, 255)).register(this);
    private final Value<Color> doubleFill = new ValueBuilder<Color>().withDescriptor("Double Fill").withValue(new Color(255, 0, 11, 100)).register(this);
    private final Value<Color> doubleLine = new ValueBuilder<Color>().withDescriptor("Double Line").withValue(new Color(255, 255, 255, 255)).register(this);
    private final Value<Boolean> voidHoles = new ValueBuilder<Boolean>().withDescriptor("Void Holes").withValue(false).register(this);
    private final Value<Color> voidSafeFill = new ValueBuilder<Color>().withDescriptor("Void Safe Fill").withValue(new Color(170, 0, 255, 25)).register(this);
    private final Value<Color> voidSafeLine = new ValueBuilder<Color>().withDescriptor("Void Safe Line").withValue(new Color(170, 0, 255, 255)).register(this);
    private final Value<Color> voidFill = new ValueBuilder<Color>().withDescriptor("Void Fill").withValue(new Color(255, 1, 242, 25)).register(this);
    private final Value<Color> voidLine = new ValueBuilder<Color>().withDescriptor("Void Line").withValue(new Color(255, 0, 251, 255)).register(this);
    private final Value<Number> voidHeight = new ValueBuilder<Number>().withDescriptor("Void Height").withValue(0.2).withRange(0.1, 2.0).register(this);
    private final ExecutorService service = Executors.newCachedThreadPool();
    private volatile List<HoleUtils.Hole> holes = new ArrayList<>();
    private volatile List<BlockPos> voidPositions = new ArrayList<>();

    public HoleESP() {
        super("HoleESP", Category.Render);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        service.submit(() -> {
            holes = HoleUtils.getHoles(mc.player, range.getValue().floatValue(), doubles.getValue(), false, false, false);
            if (voidHoles.getValue()) {
                voidPositions = getVoidHoles();
            }
        });
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        for (HoleUtils.Hole hole : holes) {
            Color fill = HoleUtils.isBedrockHole(new BlockPos(hole.getFirst().getX(), hole.getFirst().getY(), hole.getFirst().getZ())) ? bedrockFill.getValue() :
                    HoleUtils.isDoubleHole(new BlockPos(hole.getFirst().getX(), hole.getFirst().getY(), hole.getFirst().getZ())) ? doubleFill.getValue() : obbyFill.getValue();
            Color line = HoleUtils.isBedrockHole(new BlockPos(hole.getFirst().getX(), hole.getFirst().getY(), hole.getFirst().getZ())) ? bedrockLine.getValue() :
                    HoleUtils.isDoubleHole(new BlockPos(hole.getFirst().getX(), hole.getFirst().getY(), hole.getFirst().getZ())) ? doubleLine.getValue() : obbyLine.getValue();

            Box bb = HoleUtils.isDoubleHole(new BlockPos(hole.getFirst().getX(), hole.getFirst().getY(), hole.getFirst().getZ())) ?
                    new Box(hole.getFirst().getX(),
                    hole.getFirst().getY(),
                    hole.getFirst().getZ(),
                    hole.getSecond().getX() + 1,
                    hole.getSecond().getY() + 1,
                    hole.getSecond().getZ() + 1) : new Box(hole.getFirst());
            bb = new Box(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + height.getValue().doubleValue(), bb.maxZ);

            RenderUtils.drawBox(event.getMatrices(), bb, fill);
            RenderUtils.drawOutlineBox(event.getMatrices(), bb, line, lineWidth.getValue().floatValue());
        }

        if (voidHoles.getValue()) {
            for (BlockPos pos : voidPositions) {
                boolean safe = mc.world.getBlockState(pos).getBlock() != Blocks.AIR;

                Color fill = safe ? voidSafeFill.getValue() : voidFill.getValue();
                Color line = safe ? voidSafeLine.getValue() : voidLine.getValue();

                Box bb = new Box(pos);
                bb = new Box(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + voidHeight.getValue().doubleValue(), bb.maxZ);

                RenderUtils.drawBox(event.getMatrices(), bb, fill);
                RenderUtils.drawOutlineBox(event.getMatrices(), bb, line, lineWidth.getValue().floatValue());
            }
        }
    }

    public List<BlockPos> getVoidHoles() {
        ArrayList<BlockPos> voids = new ArrayList<>();
        for (int x2 = -range.getValue().intValue(); x2 < range.getValue().intValue(); ++x2) {
            for (int z2 = -range.getValue().intValue(); z2 < range.getValue().intValue(); ++z2) {
                BlockPos pos = new BlockPos((int) (mc.player.getX() + (double) x2), 0, (int) (mc.player.getZ() + (double) z2));
                if (mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)
                    continue;

                voids.add(pos);
            }
        }
        return voids;
    }

}
