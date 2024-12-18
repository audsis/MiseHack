package org.minecraft.wise.api.gui.clickgui.component.components.settings;

import org.minecraft.wise.api.gui.clickgui.ClientGuiPort;
import org.minecraft.wise.api.gui.clickgui.component.components.SettingComponent;
import org.minecraft.wise.api.gui.clickgui.component.components.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class BooleanComponent extends SettingComponent<Boolean> {

    public BooleanComponent(Value<Boolean> setting, ModuleComponent parent) {
        super(setting, parent);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Color back = new Color(25 / 255.0F, 25 / 255.0F, 25 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.isMouseOverThis(mouseX, mouseY)) {
            back = back.brighter();
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), getWidth(), getHeight(), this.getSetting().getValue() ? HudColors.INSTANCE.mainColor.getValue().getRGB() :back.getRGB());

        Color textColor = this.getSetting().getValue() ? new Color(240, 240, 240) : new Color(120, 120, 120);
        textColor = new Color(textColor.getRed() / 255.0F, textColor.getGreen() / 255.0F, textColor.getBlue() / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, this.getSetting().getName(), (int) (this.getX() + 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.getSetting().setValue(!getSetting().getValue());
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
    }

    @Override
    public void onRightRelease(int mouseX, int mouseY) {
    }

    @Override
    public void onMiddleClick(int mouseX, int mouseY) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }
}
