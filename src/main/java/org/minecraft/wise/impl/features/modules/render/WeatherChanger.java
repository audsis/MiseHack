package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class WeatherChanger extends Module {

    private final Value<String> weatherMode = new ValueBuilder<String>().withDescriptor("Weather Mode").withValue("Rain").withModes("Clear", "Rain").register(this);

    public WeatherChanger() {
        super("WeatherChanger", Category.Render);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (weatherMode.getValue().equals("Rain")) {
            mc.world.getLevelProperties().setRaining(true);
            mc.world.setRainGradient(0.3f);
        } else if (weatherMode.getValue().equals("Clear")) {
            mc.world.getLevelProperties().setRaining(false);
            mc.world.setRainGradient(0.0f);
            mc.world.setThunderGradient(0.0f);
        }
    }
}
