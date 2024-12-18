package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class Chat extends Module {
    public Value<Boolean> clear = new ValueBuilder<Boolean>().withDescriptor("Clear").withValue(true).register(this);

    public Chat() {
        super("Chat", Category.Render);
        setDescription("Customize your chat.");
    }

    @Override
    public void onEnable() {
        clear();
    }

    @Override
    public void onDisable() {
        if (mc.options.getTextBackgroundOpacity().getValue() != 0.5) {
            mc.options.getTextBackgroundOpacity().setValue(0.5);
        }
    }

    public void clear() {
        if (mc.options != null) {
            if (clear.getValue()) {
                mc.options.getTextBackgroundOpacity().setValue(0.0);
            } else {
                mc.options.getTextBackgroundOpacity().setValue(0.5);
            }
        }
    }
}
