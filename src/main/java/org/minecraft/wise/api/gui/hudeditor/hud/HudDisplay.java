package org.minecraft.wise.api.gui.hudeditor.hud;

import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.gui.hudeditor.Component;
import org.minecraft.wise.api.utils.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class HudDisplay implements Component {
    private final HudComponent hudComponent;
    private final String name;
    private int x, y, width, height, dragX, dragY;
    private boolean dragging;

    public HudDisplay(int x, int y, int width, int height, HudComponent module) {
        hudComponent = module;
        name = module.getName();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        dragging = false;
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        width = hudComponent.getWidth();
        height = hudComponent.getHeight();
        x = hudComponent.xPos.getValue().intValue();
        y = hudComponent.yPos.getValue().intValue();

        if (hudComponent.isEnabled() && !hudComponent.autoPos.getValue()) {
            if (dragging) {
                hudComponent.xPos.setValue(mouseX - this.dragX);
                hudComponent.yPos.setValue(mouseY - this.dragY);
            }

            RenderUtils.drawRect(context.getMatrices(), x, y, width, height, new Color(0, 0, 0, 100).getRGB());
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            dragging = true;
            this.dragX = (mouseX - hudComponent.xPos.getValue().intValue());
            this.dragY = (mouseY - hudComponent.yPos.getValue().intValue());
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {

    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
        if (dragging)
            dragging = false;
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
        return 0;
    }

    private boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }
}
