package org.minecraft.wise.impl.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;
import java.util.List;

import static net.minecraft.client.render.entity.LivingEntityRenderer.*;

public class Chams extends Module {
    public static Chams INSTANCE;
    public final Value<Color> color = new ValueBuilder<Color>().withDescriptor("Color").withValue(new Color(0, 150, 255, 65)).register(this);
    public final Value<Boolean> players = new ValueBuilder<Boolean>().withDescriptor("Players").withValue(true).register(this);
    private final Value<Boolean> crystals = new ValueBuilder<Boolean>().withDescriptor("Crystals").withValue(true).register(this);
    public final Value<Boolean> fill = new ValueBuilder<Boolean>().withDescriptor("Fill").withValue(true).register(this);
    public final Value<Boolean> onlyInvisible = new ValueBuilder<Boolean>().withDescriptor("OnlyInvisible").withValue(false).register(this);
    private final Value<Boolean> hurt = new ValueBuilder<Boolean>().withDescriptor("Hurt").withValue(true).register(this);
    public final Value<Color> hurtColor = new ValueBuilder<Color>().withDescriptor("Hurt Color").withValue(new Color(230, 81, 81, 65)).register(this);
    private final Value<Boolean> shine = new ValueBuilder<Boolean>().withDescriptor("Shine").withValue(true).register(this);
    public final Value<Color> friendColor = new ValueBuilder<Color>().withDescriptor("Friend Color").withValue(new Color(0, 215, 255, 65)).register(this);

    public Chams() {
        super("Chams", Category.Render);
        INSTANCE = this;
    }

    public void renderPlayer(LivingEntity entity,
                             float g,
                             MatrixStack matrixStack,
                             VertexConsumerProvider vertexConsumerProvider,
                             int i,
                             EntityModel<LivingEntity> model,
                             List<FeatureRenderer<LivingEntity, EntityModel<LivingEntity>>> features) {
        RenderUtils.setup3D();

        BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth(2.0f);

        float random;
        Direction direction;

        matrixStack.push();

        if (((hurt.getValue()) && entity.hurtTime > 0 || entity.deathTime > 0)) {
            RenderSystem.setShaderColor(hurtColor.getValue().getRed() / 255.0f,
                    hurtColor.getValue().getGreen() / 255.0f,
                    hurtColor.getValue().getBlue() / 255.0f,
                    hurtColor.getValue().getAlpha() / 255.0f);
        } else if (FriendManager.INSTANCE.isFriend(entity)) {
            RenderSystem.setShaderColor(friendColor.getValue().getRed() / 255.0f,
                    friendColor.getValue().getGreen() / 255.0f,
                    friendColor.getValue().getBlue() / 255.0f,
                    friendColor.getValue().getAlpha() / 255.0f);
        } else {
            RenderSystem.setShaderColor(color.getValue().getRed() / 255.0f,
                    color.getValue().getGreen() / 255.0f,
                    color.getValue().getBlue() / 255.0f,
                    color.getValue().getAlpha() / 255.0f);
        }

        if (shine.getValue())
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        else
            RenderSystem.defaultBlendFunc();

        model.handSwingProgress = entity.getHandSwingProgress(g);
        model.riding = entity.hasVehicle();
        model.child = entity.isBaby();

        float bodyYaw = MathHelper.lerpAngleDegrees(g, entity.prevBodyYaw, entity.bodyYaw);
        float headYaw = MathHelper.lerpAngleDegrees(g, entity.prevHeadYaw, entity.headYaw);
        float differences = headYaw - bodyYaw;

        if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity living) {

            bodyYaw = MathHelper.lerpAngleDegrees(g, living.prevBodyYaw, living.bodyYaw);
            differences = headYaw - bodyYaw;

            float deg = MathHelper.wrapDegrees(differences);

            if (deg < -85.0f)
                deg = -85.0f;

            if (deg >= 85.0f)
                deg = 85.0f;

            bodyYaw = headYaw - deg;

            if (deg * deg > 2500.0f)
                bodyYaw += deg * 0.2f;

            differences = headYaw - bodyYaw;
        }

        float pitch = MathHelper.lerp(g, entity.prevPitch, entity.getPitch());

        if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
            pitch *= -1.0f;
            differences *= -1.0f;
        }

        if (entity.isInPose(EntityPose.SLEEPING) && (direction = entity.getSleepingDirection()) != null) {
            random = entity.getEyeHeight(EntityPose.STANDING) - 0.1f;

            matrixStack.translate((float) (-direction.getOffsetX()) * random, 0.0f, (float) (-direction.getOffsetZ()) * random);
        }

        float af = entity.getScale();
        float l = entity.age + g;

        setupTransforms(entity, matrixStack, bodyYaw, g, af);

        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);

        random = 0.0f;
        float pos = 0.0f;

        if (!entity.hasVehicle() && entity.isAlive()) {
            random = entity.limbAnimator.getSpeed(g);
            pos = entity.limbAnimator.getPos(g);

            if (entity.isBaby())
                pos *= 3.0f;

            if (random > 1.0f)
                random = 1.0f;
        }

        model.animateModel(entity, pos, random, g);
        model.setAngles(entity, pos, random, l, differences, pitch);

        model.render(matrixStack, bb, i, LivingEntityRenderer.getOverlay(entity, 0), -1);

        BufferRenderer.drawWithGlobalProgram(bb.end());

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.end3D();

        if (!entity.isSpectator()) {
            for (FeatureRenderer featureRenderer : features)
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, entity, pos, random, g, l, differences, pitch);
        }

        matrixStack.pop();
    }

    protected void setupTransforms(LivingEntity entity, MatrixStack matrices, float bodyYaw, float tickDelta, float scale) {
        if (isShaking(entity))
            bodyYaw += (float) (Math.cos((double) entity.age * (double) 3.25F) * Math.PI * (double) 0.4F);

        if (!entity.isInPose(EntityPose.SLEEPING))
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);

            if (f > 1.0F)
                f = 1.0F;

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * getLyingAngle()));
        } else if (entity.isUsingRiptide()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float) entity.age + tickDelta) * -75.0F));
        } else if (entity.isInPose(EntityPose.SLEEPING)) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getLyingAngle()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
        } else if (shouldFlipUpsideDown(entity)) {
            matrices.translate(0.0F, (entity.getHeight() + 0.1F) / scale, 0.0F);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        }

    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH -> {
                return 90.0F;
            }
            case NORTH -> {
                return 270.0F;
            }
            case EAST -> {
                return 180.0F;
            }
            default -> {
                return 0.0F;
            }
        }
    }

    protected boolean isShaking(LivingEntity entity) {
        return entity.isFrozen();
    }

    protected float getLyingAngle() {
        return 90.0F;
    }

}
