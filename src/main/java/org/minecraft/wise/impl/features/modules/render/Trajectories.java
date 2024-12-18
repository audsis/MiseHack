package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;

public class Trajectories extends Module {

    private final Value<Color> fill = new ValueBuilder<Color>().withDescriptor("Fill").withValue(new Color(255, 62, 62, 65)).register(this);
    private final Value<Color> line = new ValueBuilder<Color>().withDescriptor("Line").withValue(new Color(255, 62, 62, 255)).register(this);
    private final Value<Color> outline = new ValueBuilder<Color>().withDescriptor("Outline").withValue(new Color(255, 62, 62, 255)).register(this);
    private final Value<Number> outlineWidth = new ValueBuilder<Number>().withDescriptor("OutlineWidth").withValue(0.5f).withRange(0.0f, 5.0f).register(this);
    private final Value<Number> scale = new ValueBuilder<Number>().withDescriptor("Scale").withValue(0.5f).withRange(0.1f, 3.0f).register(this);

    public Trajectories()
    {
        super("Trajectories", Category.Render);
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (mc.player == null || mc.world == null || !mc.options.getPerspective().isFirstPerson())
            return;

        Item item = mc.player.getMainHandStack().getItem();

        if (isThrowable(item)) {
            double x = RenderUtils.interpolate(mc.player.prevX, mc.player.getX(), mc.getRenderTickCounter().getTickDelta(true));
            double y = RenderUtils.interpolate(mc.player.prevY, mc.player.getY(), mc.getRenderTickCounter().getTickDelta(true));
            double z = RenderUtils.interpolate(mc.player.prevZ, mc.player.getZ(), mc.getRenderTickCounter().getTickDelta(true));

            float yaw = mc.player.getYaw();
            float pitch = 0;

            float velocity = 1.5f;

            if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem)
                velocity = 0.5f;

            if (item instanceof ExperienceBottleItem)
                velocity = 0.59f;

            if (item instanceof TridentItem)
                velocity = 2f;

            if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem || item instanceof ExperienceBottleItem)
                pitch = 20;

            y = y + mc.player.getEyeHeight(mc.player.getPose()) - 0.1000000014901161;

            if (item == mc.player.getMainHandStack().getItem()) {
                x = x - MathHelper.cos(yaw / 180.0f * 3.1415927f) * 0.16f;
                z = z - MathHelper.sin(yaw / 180.0f * 3.1415927f) * 0.16f;
            } else {
                x = x + MathHelper.cos(yaw / 180.0f * 3.1415927f) * 0.16f;
                z = z + MathHelper.sin(yaw / 180.0f * 3.1415927f) * 0.16f;
            }

            float maxDist = item instanceof BowItem ? 1.0f : 0.4f;

            double motionX = -MathHelper.sin(yaw / 180.0f * 3.1415927f) *
                    MathHelper.cos(mc.player.getPitch() / 180.0f * 3.1415927f) *
                    maxDist;
            double motionY = -MathHelper.sin((mc.player.getPitch() - pitch) / 180.0f * 3.141593f) * maxDist;
            double motionZ = MathHelper.cos(yaw / 180.0f * 3.1415927f) *
                    MathHelper.cos(mc.player.getPitch() / 180.0f * 3.1415927f) *
                    maxDist;

            float power = mc.player.getItemUseTime() / 20.0f;
            power = (power * power + power * 2.0f) / 3.0f;

            if (power > 1.0f)
                power = 1.0f;

            float distance = MathHelper.sqrt((float) (motionX * motionX + motionY * motionY + motionZ * motionZ));

            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;

            float pow = (item instanceof BowItem ? (power * 2.0f) : item instanceof CrossbowItem ? (2.2f) : 1.0f) * velocity;

            motionX *= pow;
            motionY *= pow;
            motionZ *= pow;

            motionY += mc.player.getVelocity().getY();


            Vec3d lastPos;

            for (int i = 0; i < 300; i++) {
                lastPos = new Vec3d(x, y, z);

                x += motionX;
                y += motionY;
                z += motionZ;

                if (mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).getBlock() == Blocks.WATER) {
                    motionX *= 0.8;
                    motionY *= 0.8;
                    motionZ *= 0.8;
                } else {
                    motionX *= 0.99;
                    motionY *= 0.99;
                    motionZ *= 0.99;
                }

                if (item instanceof BowItem)
                    motionY -= 0.05000000074505806;
                else if (mc.player.getMainHandStack().getItem() instanceof CrossbowItem)
                    motionY -= 0.05000000074505806;
                else
                    motionY -= 0.03f;


                Vec3d pos = new Vec3d(x, y, z);

                if (y <= -65)
                    break;

                if (motionX == 0 && motionY == 0 && motionZ == 0)
                    continue;

                RenderUtils.drawLine(lastPos, pos, line.getValue());

                BlockHitResult result = mc.world.raycast(new RaycastContext(lastPos,
                        pos,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        mc.player));

                if (result != null && result.getType() == HitResult.Type.BLOCK) {
                    event.getMatrices().push();
                    Vec3d hitPos = result.getPos();

                    event.getMatrices().translate(hitPos.x - mc.getEntityRenderDispatcher().camera.getPos().x,
                            hitPos.y - mc.getEntityRenderDispatcher().camera.getPos().y,
                            hitPos.z - mc.getEntityRenderDispatcher().camera.getPos().z);

                    int land = result.getSide().getId();

                    switch (land) {
                        case 1:
                            event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
                            break;
                        case 2, 5:
                            event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                            break;
                        case 3, 4:
                            event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
                            break;
                    }

                    event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));

                    event.getMatrices().scale(scale.getValue().floatValue() / 10, scale.getValue().floatValue() / 10, scale.getValue().floatValue() / 10);

                    RenderUtils.drawUnfilledCircle(event.getMatrices(), 0, 0, 0, 10, outlineWidth.getValue().floatValue(), outline.getValue().getRGB());
                    RenderUtils.drawFilledCircle(event.getMatrices(), 0, 0, 0, 10, fill.getValue().getRGB());

                    event.getMatrices().pop();
                    break;
                }
            }
        }
    }

    private boolean isThrowable(Item item) {
        return item instanceof EnderPearlItem ||
                item instanceof TridentItem ||
                item instanceof ExperienceBottleItem ||
                item instanceof SnowballItem ||
                item instanceof EggItem ||
                item instanceof SplashPotionItem ||
                item instanceof LingeringPotionItem;
    }

}
