package org.minecraft.wise.api.utils.render.shader.post;

import org.minecraft.wise.api.utils.render.shader.ShapeMode;
import org.minecraft.wise.impl.features.modules.render.Shaders;
import net.minecraft.entity.Entity;

public class EntityOutlineShader extends EntityShader {
    private static Shaders shaders;

    public EntityOutlineShader() {
        init("glow_outline");
    }

    @Override
    protected boolean shouldDraw() {
        if (shaders == null) {
            shaders = Shaders.INSTANCE;
        }
        return shaders.isShader();
    }

    @Override
    public boolean shouldDraw(Entity entity) {
        if (!shouldDraw()) {
            return false;
        }
        return !shaders.shouldSkip(entity);
    }

    @Override
    protected void setUniforms() {
        shader.set("u_Width", Shaders.INSTANCE.glow.getValue().intValue());
        shader.set("u_FillOpacity", 0.3);
        shader.set("u_ShapeMode", ShapeMode.Both.ordinal());
        shader.set("u_GlowMultiplier", Shaders.INSTANCE.glow.getValue().floatValue());
        shader.set("u_DynamicRainbow", Shaders.INSTANCE.rainbow.getValue());
        shader.set("u_RainbowStrength", -0.0033333334f, -0.0033333334f);
        shader.set("u_RainbowSpeed", Shaders.INSTANCE.speed.getValue().floatValue());
        shader.set("u_Saturation", Shaders.INSTANCE.saturation.getValue().floatValue());
    }
}

