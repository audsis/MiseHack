package org.minecraft.wise.api.gui.hudeditor.component.settings;


import org.minecraft.wise.api.gui.hudeditor.HudGuiPort;
import org.minecraft.wise.api.gui.hudeditor.component.SettingComponent;
import org.minecraft.wise.api.gui.hudeditor.component.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.awt.*;

public final class EnumComponent extends SettingComponent<String> {

    public EnumComponent(Value<String> setting, ModuleComponent parent) {
        super(setting, parent);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Color back = new Color(25 / 255.0F, 25 / 255.0F, 25 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.isMouseOverThis(mouseX, mouseY)) {
            back = back.brighter();
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), getWidth(), getHeight(), back.getRGB());

        String value = this.getSetting().getValue();
        Color textColor = new Color(240 / 255.0F, 240 / 255.0F, 240 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, this.getSetting().getName() + ": " + Formatting.GRAY + value, (int) (this.getX() + 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            int current = 0;
            int index = 0;
            String[] modes = getSetting().getModes();
            for (String s2 : modes) {
                if (s2.equals(getSetting().getValue())) {
                    current = index;
                }
                ++index;
            }

            int amount = 1;
            if (current + amount > modes.length - 1) {
                this.getSetting().setValue(modes[0]);
            } else {
                this.getSetting().setValue(modes[current + amount]);
            }
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
//        if (this.isMouseOverThis(mouseX, mouseY)) {
//            this.playClickSound();
//            this.decrement();
//        }
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