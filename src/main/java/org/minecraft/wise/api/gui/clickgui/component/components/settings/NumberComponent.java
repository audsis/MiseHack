package org.minecraft.wise.api.gui.clickgui.component.components.settings;

import org.minecraft.wise.api.gui.clickgui.ClientGuiPort;
import org.minecraft.wise.api.gui.clickgui.component.components.SettingComponent;
import org.minecraft.wise.api.gui.clickgui.component.components.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.render.animations.Easing;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberComponent extends SettingComponent<Number> {

    private boolean dragging;

    private long firstRenderTime = 0L;

    public NumberComponent(Value<Number> setting, ModuleComponent parent) {
        super(setting, parent);

    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Color back = new Color(25 / 255.0F, 25 / 255.0F, 25 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        if (this.isMouseOverThis(mouseX, mouseY)) {
            back = back.brighter();
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), getWidth(), getHeight(), back.getRGB());

        Value raw = this.getSetting();
        if (this.dragging) {
            float width = Math.max(mouseX - this.getX(), 0.0F);
            width = Math.min(width, getWidth()) / getWidth();
            Value<Number> intSetting = (Value<Number>) raw;
            float max = (int) intSetting.getMax().floatValue() - (int) intSetting.getMin().floatValue();
            float newValue = (width * max) + intSetting.getMin().floatValue();
            intSetting.setValue(newValue);
        }

        float percentFilled = 0.0F;
        Value<Number> floatSetting = (Value<Number>) raw;

        float value = floatSetting.getValue().floatValue() - floatSetting.getMin().floatValue();
        float max = floatSetting.getMax().floatValue() - floatSetting.getMin().floatValue();

        percentFilled = value / max;

        float sliderWidth = (getWidth() * percentFilled) - 2.0F;
        if (this.firstRenderTime == 0L) {
            this.firstRenderTime = System.currentTimeMillis();
        }
        long diff = System.currentTimeMillis() - firstRenderTime;
        float progress = Easing.exponential(diff, 0.0F, 1.0F, 1000L);
        sliderWidth *= progress;
        sliderWidth = Math.min(sliderWidth, getWidth() - 2.0F);

        RenderUtils.drawRect(context.getMatrices(), this.getX() + 1.0F, this.getY(), sliderWidth, getHeight(), HudColors.INSTANCE.mainColor.getValue().getRGB());

        Color textColor = new Color(240 / 255.0F, 240 / 255.0F, 240 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, this.getSetting().getName(), (int) (this.getX() + 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));

        String sasasas;
        Value<Number> sasasa = (Value<Number>) raw;
        sasasas = String.valueOf(roundFloat(sasasa.getValue().floatValue(), 2));

        Color valueColor = new Color(120 / 255.0F, 120 / 255.0F, 120 / 255.0F, ClientGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, sasasas, (int) (this.getX() + getWidth() - FontManager.getWidth(sasasas) - 3.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(valueColor));
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.dragging = !this.dragging;
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
        this.dragging = false;
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

    private double roundDouble(double number, int scale) {
        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private float roundFloat(float number, int scale) {
        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    private enum NumberType {
        INTEGER,
        DOUBLE,
        FLOAT
    }
}