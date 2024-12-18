package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;

import java.awt.*;

public class BlockHighlight extends Module {

    public static BlockHighlight INSTANCE;
    private final Value<Color> fill = new ValueBuilder<Color>().withDescriptor("Fill").withValue(new Color(255, 62, 62, 25)).register(this);
    private final Value<Color> line = new ValueBuilder<Color>().withDescriptor("Line").withValue(new Color(255, 62, 62, 255)).register(this);
    private final Value<Boolean> entity = new ValueBuilder<Boolean>().withDescriptor("Entity").withValue(false).register(this);
    private final Value<Color> entityFill = new ValueBuilder<Color>().withDescriptor("EntityFill").withValue(new Color(255, 62, 62, 25)).register(this);
    private final Value<Color> entityLine = new ValueBuilder<Color>().withDescriptor("EntityLine").withValue(new Color(255, 62, 62, 255)).register(this);

    public BlockHighlight() {
        super("BlockHighlight", Category.Render);
        INSTANCE = this;
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (mc.crosshairTarget instanceof BlockHitResult result) {
            Block block = mc.world.getBlockState(result.getBlockPos()).getBlock();
            if (block instanceof AirBlock || block instanceof FluidBlock)
                return;

            RenderUtils.drawBox(event.getMatrices(), new Box(result.getBlockPos()), fill.getValue());
            RenderUtils.drawOutlineBox(event.getMatrices(), new Box(result.getBlockPos()), line.getValue(), 1.0f);
        } else if (mc.crosshairTarget instanceof EntityHitResult result && entity.getValue()) {
            Box bb = result.getEntity().getBoundingBox();

            RenderUtils.drawBox(event.getMatrices(), bb, entityFill.getValue());
            RenderUtils.drawOutlineBox(event.getMatrices(), bb, entityLine.getValue(), 1.0f);
        }
    }
}
