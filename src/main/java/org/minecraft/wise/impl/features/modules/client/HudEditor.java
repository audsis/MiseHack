package org.minecraft.wise.impl.features.modules.client;

import org.minecraft.wise.api.gui.clickgui.ClientGuiPort;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.gui.hudeditor.HudGui;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

public class HudEditor extends Module {

    public final Value<Boolean> lowercase = new ValueBuilder<Boolean>().withDescriptor("Lowercase").withValue(false).register(this);
    public static HudEditor INSTANCE;

    public HudEditor() {
        super("HudEditor", Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ClientGuiPort.getInstance().setGuiClosed(false);
        ClientGuiPort.getInstance().setOpenTime(System.currentTimeMillis());
        mc.setScreen(HudGui.instance);
        setEnabled(false);
    }
}
