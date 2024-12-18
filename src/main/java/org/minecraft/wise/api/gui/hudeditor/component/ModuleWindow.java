package org.minecraft.wise.api.gui.hudeditor.component;

import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.gui.hudeditor.HudGuiPort;
import org.minecraft.wise.api.gui.hudeditor.DraggableComponent;
import org.minecraft.wise.api.gui.hudeditor.component.module.ModuleComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.render.animations.Easing;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ModuleWindow extends DraggableComponent {

    private static final float HEIGHT = 17.0F;

    private static final float WIDTH = 100.0F;

    private final Feature.Category category;

    private final List<ModuleComponent> moduleComponents = new ArrayList<>();

    private float currentAnimationProgress;

    private boolean opened = true;

    private long openTime = System.currentTimeMillis();

    public ModuleWindow(float x, float y, Feature.Category category) {
        super(x, y);
        this.category = category;
        for (Feature module : category.getModules()) {
            ModuleComponent moduleComponent = new ModuleComponent(module, this);
            this.moduleComponents.add(moduleComponent);
        }
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (this.isLeftMouseHeld() && this.isMouseOverThis(mouseX, mouseY)) {
            if (HudGuiPort.getInstance().getDraggingWindow() == null) {
                HudGuiPort.getInstance().setDraggingWindow(this);
            }
        }

//        Color syncTopLeft = Colors.getInstance().getSyncedColor(this.getX(), this.getY(), ClientGuiPort.getInstance().getColorTransparency());
//        Color syncBottomLeft = Colors.getInstance().getSyncedColor(this.getX(), this.getY() + HEIGHT, ClientGuiPort.getInstance().getColorTransparency());
//        Color syncBottomRight = Colors.getInstance().getSyncedColor(this.getX() + WIDTH, this.getY() + HEIGHT, ClientGuiPort.getInstance().getColorTransparency());
//        Color syncTopRight = Colors.getInstance().getSyncedColor(this.getX() + WIDTH, this.getY(), ClientGuiPort.getInstance().getColorTransparency());
//
//        if (this.isMouseOverThis(mouseX, mouseY)) {
//            syncTopLeft = syncTopLeft.brighter();
//            syncBottomLeft = syncBottomLeft.brighter();
//            syncBottomRight = syncBottomRight.brighter();
//            syncTopRight = syncTopRight.brighter();
//        }

        RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY(), WIDTH, HEIGHT, HudColors.INSTANCE.mainColor.getValue().getRGB());

        Color textColor = new Color(240 / 255.0F, 240 / 255.0F, 240 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, category.name(), (int) (this.getX() + 2.0F), (int) (this.getY() + 4.0F), ColorUtil.getRGBA(textColor));

        float currY = HEIGHT;
        float targetY = 0.0F;
        float diff = System.currentTimeMillis() - this.openTime;
        for (ModuleComponent moduleComponent : this.moduleComponents) {
            targetY += moduleComponent.getHeight();
        }

        if (this.opened) {
            this.currentAnimationProgress = Easing.exponential(diff, 0.0F, targetY, 500L);
            if (this.currentAnimationProgress < targetY) {
                RenderUtils.prepareScissor(context, (int) this.getX(), (int) (this.getY() + HEIGHT), (int) (this.getX() + WIDTH), (int) (this.getY() + HEIGHT + this.currentAnimationProgress));
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            for (ModuleComponent moduleComponent : this.moduleComponents) {
                moduleComponent.setY(this.getY() + currY);
                currY += moduleComponent.getHeight();
                moduleComponent.setY(moduleComponent.getY() - targetY);
                moduleComponent.setY(moduleComponent.getY() + this.currentAnimationProgress);
                moduleComponent.drawScreen(context, mouseX, mouseY, partialTicks);
            }

            if (this.currentAnimationProgress < targetY) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
//                GL11.glPopAttrib();
            }

            Color greyish = new Color(64 / 255.0F, 64 / 255.0F, 64 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 700.0F);
            RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY() + HEIGHT, WIDTH, HEIGHT / 2.0F, greyish.getRGB(), 0x00, 0x00, greyish.getRGB());
        } else {
            if (this.currentAnimationProgress > 0.0F) {
                RenderUtils.prepareScissor(context, (int) this.getX(), (int) (this.getY() + HEIGHT), (int) (this.getX() + WIDTH), (int) (this.getY() + HEIGHT + this.currentAnimationProgress));
                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                float reverseProgress = Easing.exponential(diff, 0.0F, targetY, 500L);
                this.currentAnimationProgress = targetY - reverseProgress;

                for (ModuleComponent moduleComponent : this.moduleComponents) {
                    moduleComponent.setY(this.getY() + currY);
                    currY += moduleComponent.getHeight();
                    moduleComponent.setY(moduleComponent.getY() - reverseProgress);
                    moduleComponent.drawScreen(context, mouseX, mouseY, partialTicks);
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);

                Color greyish = new Color(64 / 255.0F, 64 / 255.0F, 64 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 700.0F);
                RenderUtils.drawRect(context.getMatrices(), this.getX(), this.getY() + HEIGHT, WIDTH, HEIGHT / 2.0F, greyish.getRGB(), 0x00, 0x00, greyish.getRGB());
            }
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.onLeftClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.opened = !this.opened;
            this.openTime = System.currentTimeMillis();
        } else if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.onRightClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
        HudGuiPort.getInstance().setDraggingWindow(null);
        if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.onLeftRelease(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRightRelease(int mouseX, int mouseY) {
        if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.onRightRelease(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onMiddleClick(int mouseX, int mouseY) {
        if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.onMiddleClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.opened) {
            for (ModuleComponent component : this.moduleComponents) {
                component.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public float getHeight() {
        return 16.0F;
    }

    private boolean isMouseOverThis(float mouseX, float mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + WIDTH && mouseY >= this.getY() && mouseY <= this.getY() + HEIGHT;
    }

    public boolean isMouseOverThisTab(float mouseX, float mouseY) {
        float moduleHeight = 0.0F;
        if (this.opened) {
            for (ModuleComponent moduleComponent : this.moduleComponents) {
                moduleHeight += moduleComponent.getHeight();
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + WIDTH && mouseY >= this.getY() && mouseY <= this.getY() + HEIGHT + moduleHeight;
    }

    public List<ModuleComponent> getModuleComponents() {
        return this.moduleComponents;
    }

    public boolean isOpened() {
        return this.opened;
    }
}