package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class NoRender extends Module {

    public static NoRender INSTANCE;
    public Value<Boolean> blindness = new ValueBuilder<Boolean>().withDescriptor("Blindness").withValue(true).register(this);
    public Value<Boolean> nausea = new ValueBuilder<Boolean>().withDescriptor("Nausea").withValue(true).register(this);
    public Value<Boolean> fog = new ValueBuilder<Boolean>().withDescriptor("Fog").withValue(true).register(this);
    public Value<Boolean> noHurtCam = new ValueBuilder<Boolean>().withDescriptor("NoHurtCam").withValue(true).register(this);
    public Value<Boolean> deepDark = new ValueBuilder<Boolean>().withDescriptor("DeepDark").withValue(true).register(this);
    public Value<Boolean> vignette = new ValueBuilder<Boolean>().withDescriptor("Vignette").withValue(true).register(this);
    public Value<Boolean> fireOverlay = new ValueBuilder<Boolean>().withDescriptor("Fire").withValue(true).register(this);
    public Value<Boolean> explosions = new ValueBuilder<Boolean>().withDescriptor("Explosions").withValue(true).register(this);
    public Value<Boolean> potionHud = new ValueBuilder<Boolean>().withDescriptor("PotionHud").withValue(true).register(this);
    public Value<Boolean> fireworks = new ValueBuilder<Boolean>().withDescriptor("Fireworks").withValue(false).register(this);
    public Value<Boolean> liquidVision = new ValueBuilder<Boolean>().withDescriptor("LiquidVision").withValue(true).register(this);
    public Value<Boolean> breakParticles = new ValueBuilder<Boolean>().withDescriptor("BreakParticles").withValue(true).register(this);
    
    public NoRender() {
        super("NoRender", Category.Render);
        INSTANCE = this;
    }
}
