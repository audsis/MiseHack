package org.minecraft.wise.api.feature.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.wrapper.IMinecraft;

public class HudComponent
        extends Feature
        implements IMinecraft {
    public final Value<Boolean> autoPos = new ValueBuilder<Boolean>().withDescriptor("AutoPos").withValue(false).register(this);
    public final Value<Number> xPos = new ValueBuilder<Number>().withDescriptor("X Pos").withValue(100).withRange(0, 1000).register(this);
    public final Value<Number> yPos = new ValueBuilder<Number>().withDescriptor("Y Pos").withValue(10).withRange(0, 1000).register(this);
    protected int height = 9;
    protected int width = 30;

    public HudComponent(String name) {
        super(name, Feature.Category.Hud, Feature.FeatureType.Hud);
        Bus.EVENT_BUS.register(this);
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        xPos.setMax(event.getContext().getScaledWindowWidth());
        yPos.setMax(event.getContext().getScaledWindowHeight());
        xPos.setMin(0);
        yPos.setMin(0);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}