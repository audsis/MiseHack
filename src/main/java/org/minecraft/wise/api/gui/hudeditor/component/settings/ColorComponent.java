package org.minecraft.wise.api.gui.hudeditor.component.settings;

import org.minecraft.wise.api.gui.hudeditor.HudGuiPort;
import org.minecraft.wise.api.gui.hudeditor.component.SettingComponent;
import org.minecraft.wise.api.gui.hudeditor.component.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.math.MathUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ColorComponent extends SettingComponent<Color> {
    private boolean opened;
    private boolean draggingHue;
    private boolean draggingColor;
    private boolean draggingAlpha;
    private float pickerHeight;

    public ColorComponent(Value<Color> setting, ModuleComponent parent) {
        super(setting, parent);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        float[] hsb = Color.RGBtoHSB(this.getSetting().getValue().getRed(), this.getSetting().getValue().getGreen(), this.getSetting().getValue().getBlue(), null);
        int clampMouseX;

        Color back = new Color(25 / 255.0F, 25 / 255.0F, 25 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.isMouseOverThis(mouseX, mouseY)) {
            back = back.brighter();
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), getWidth(), getHeight(), back.getRGB());

        RenderUtils.drawRect(context.getMatrices(), (int) (this.getX() + getWidth() - 13 - 3.0F), (int) (this.getY() + 2.0F), 11, 11, this.getSetting().getValue().getRGB());

        Color textColor = new Color(240 / 255.0F, 240 / 255.0F, 240 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, this.getSetting().getName(), (int) (this.getX() + 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));

        pickerHeight = 110.0F;
        int color = Color.HSBtoRGB(hsb[0], 1.0f, 1.0f);

        if (opened) {
            RenderUtils.drawGradientQuad(context.getMatrices(), this.getX(), this.getY() + pickerHeight, this.getX() + getWidth(), this.getY() - (pickerHeight + 16.0f), 0xffffffff, color, true);
            RenderUtils.drawGradientQuad(context.getMatrices(), this.getX(), this.getY() + pickerHeight, this.getX() + getWidth(), this.getY() - (pickerHeight + 16.0f), 0, 0xff000000, false);

            if (this.draggingColor) {
                clampMouseX = (int) MathHelper.clamp(mouseX, this.getX(), this.getX() + getWidth());
                float normalX = (float) MathUtil.normalize(clampMouseX, this.getX(),this.getX() + getWidth());
                int clampMouseY = (int) MathHelper.clamp(mouseY, this.getY(), this.getY() - (pickerHeight - 16.0f));
                float normalY = (float) MathUtil.normalize(clampMouseY, this.getY(), this.getY() - (pickerHeight - 16.0f));
                normalY = -normalY + 1.0f;
                normalY = MathHelper.clamp(normalY, 0.0f, 1.0f);
                this.getSetting().setValue(ColorUtil.newAlpha(Color.getHSBColor(hsb[0], normalX, normalY), this.getSetting().getValue().getAlpha()));
            }
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (opened) {
            draggingColor = true;
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.opened = !this.opened;
        }
    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
        draggingColor = false;
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

    @Override
    public float getHeight() {
        if (!opened) {
            return 16.0F;
        }
        return pickerHeight;
    }
}
