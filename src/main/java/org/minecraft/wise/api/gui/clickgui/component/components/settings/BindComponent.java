package org.minecraft.wise.api.gui.clickgui.component.components.settings;

import org.minecraft.wise.api.gui.clickgui.ClientGuiPort;
import org.minecraft.wise.api.gui.clickgui.component.components.SettingComponent;
import org.minecraft.wise.api.gui.clickgui.component.components.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.keyboard.KeyUtils;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.custom.Bind;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class BindComponent extends SettingComponent<Bind> {

    private boolean binding;

    public BindComponent(Value<Bind> setting, ModuleComponent parent) {
        super(setting, parent);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Color back = new Color(25 / 255.0F, 25 / 255.0F, 25 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.isMouseOverThis(mouseX, mouseY)) {
            back = back.brighter();
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), getWidth(), getHeight(), back.getRGB());

        Color valueColor = new Color(120 / 255.0F, 120 / 255.0F, 120 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.binding) {
            FontManager.drawText(context, "Listening...", (int) (this.getX() + 5.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(valueColor));
        } else {
            Color textColor = new Color(240 / 255.0F, 240 / 255.0F, 240 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
            FontManager.drawText(context, "Bind", (int) (this.getX() + 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));

            String value = KeyUtils.getKeyName(this.getSetting().getValue().getKey());

            FontManager.drawText(context, value, (int) (this.getX() + getWidth() - FontManager.getWidth(value) - 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(valueColor));
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.binding = !binding;
        } else {
            this.binding = false;
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
        if (this.binding) {
            this.playClickSound();
            this.getSetting().getValue().setKey(2);
            this.binding = false;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.binding && keyCode != -1) {
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode ==  GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                keyCode = -1;
            }
            this.playClickSound();
            this.getSetting().getValue().setKey(keyCode);
            this.binding = false;
        }
    }
}