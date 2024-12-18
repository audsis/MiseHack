package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.gui.clickgui.ClientGui;
import org.minecraft.wise.api.gui.clickgui.ClientGuiPort;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class ClickGUI extends Module {
    public static ClickGUI INSTANCE;
    public final Value<Boolean> closeAnimation = new ValueBuilder<Boolean>().withDescriptor("CloseAnimation").withValue(true).register(this);

    public ClickGUI() {
        super("ClickGui", Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ClientGuiPort.getInstance().setGuiClosed(false);
        ClientGuiPort.getInstance().setOpenTime(System.currentTimeMillis());
        mc.setScreen(ClientGui.instance);
    }
}
