package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.entity.EntityUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.awt.*;

public class Shaders extends Module {

    public final Value<Color> color = new ValueBuilder<Color>().withDescriptor("Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Boolean> rainbow = new ValueBuilder<Boolean>().withDescriptor("Rainbow").withValue(false).register(this);
    public final Value<Number> speed = new ValueBuilder<Number>().withDescriptor("Speed").withValue(0.4f).withRange(0.0f, 1.0f).register(this);
    public final Value<Number> saturation = new ValueBuilder<Number>().withDescriptor("Saturation").withValue(0.5f).withRange(0.0f, 1.0f).register(this);

    public final Value<Number> glow = new ValueBuilder<Number>().withDescriptor("Glow").withValue(3.0f).withRange(0.0f, 10.0f).register(this);

    public final Value<Boolean> players = new ValueBuilder<Boolean>().withDescriptor("Players").withValue(true).register(this);
    public final Value<Boolean> items = new ValueBuilder<Boolean>().withDescriptor("Items").withValue(true).register(this);
    public final Value<Boolean> crystals = new ValueBuilder<Boolean>().withDescriptor("Crystals").withValue(true).register(this);
    public final Value<Boolean> tnt = new ValueBuilder<Boolean>().withDescriptor("TNT").withValue(true).register(this);
    public final Value<Boolean> fallingBlocks = new ValueBuilder<Boolean>().withDescriptor("Falling Blocks").withValue(true).register(this);
    public final Value<Boolean> pearls = new ValueBuilder<Boolean>().withDescriptor("Pearls").withValue(true).register(this);
    public final Value<Boolean> ignoreSelf = new ValueBuilder<Boolean>().withDescriptor("IgnoreSelf").withValue(false).register(this);
    public static Shaders INSTANCE;

    public Shaders() {
        super("Shaders", Category.Render);
        INSTANCE = this;
        setDescription("Better ESP");
    }

    public boolean shouldSkip(Entity entity) {
        return isExcludedEntity(entity) ||
                (entity == mc.player && ignoreSelf.getValue()) ||
                (entity == mc.cameraEntity && mc.options.getPerspective().isFirstPerson()) ||
                !EntityUtils.isInRenderDistance(entity);
    }

    private boolean isExcludedEntity(Entity entity) {
        EntityType<?> type = entity.getType();

        if (type.equals(EntityType.PLAYER)) {
            return !players.getValue();
        }

        if (type.equals(EntityType.ITEM)) {
            return !items.getValue();
        }

        if (type.equals(EntityType.TNT)) {
            return !tnt.getValue();
        }

        if (type.equals(EntityType.FALLING_BLOCK)) {
            return !fallingBlocks.getValue();
        }

        if (type.equals(EntityType.ENDER_PEARL)) {
            return !pearls.getValue();
        }

        return !type.equals(EntityType.END_CRYSTAL) || !crystals.getValue();
    }

    public boolean isShader() {
        return isEnabled() && !NullUtils.nullCheck();
    }
}
