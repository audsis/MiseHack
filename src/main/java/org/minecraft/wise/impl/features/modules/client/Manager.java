package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class Manager extends Module {
    public static Manager INSTANCE;
    public final Value<Boolean> bold = new ValueBuilder<Boolean>().withDescriptor("Bold").withValue(true).register(this);
    public final Value<Boolean> notifications = new ValueBuilder<Boolean>().withDescriptor("Notifications").withValue(false).register(this);
    public final Value<Boolean> moduleNotifications = new ValueBuilder<Boolean>().withDescriptor("ModuleNotifications").withValue(false).register(this);
    public final Value<Boolean> antiAlias = new ValueBuilder<Boolean>().withDescriptor("AntiAlias").withValue(true).register(this);
    public final Value<String> multiplier = new ValueBuilder<String>().withDescriptor("Multiplier").withValue("2x").withModes("2x", "4x", "8x").register(this);
    public final Value<String> silent = new ValueBuilder<String>().withDescriptor("Silent").withValue("Normal").withModes("Normal", "Alternative").register(this);

    public Manager() {
        super("Manager", Category.Client);
        INSTANCE = this;
        setDescription("Manages certain aspects of Wisehack.");
    }

    public int getMultiplier() {
        switch (multiplier.getValue()) {
            case "2x" -> {
                return 2;
            }
            case "4x" -> {
                return 4;
            }
        }

        return 8;
    }

}
