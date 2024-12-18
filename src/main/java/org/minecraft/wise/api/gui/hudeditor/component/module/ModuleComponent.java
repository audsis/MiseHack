package org.minecraft.wise.api.gui.hudeditor.component.module;

import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.gui.hudeditor.HudGuiPort;
import org.minecraft.wise.api.gui.hudeditor.Component;
import org.minecraft.wise.api.gui.hudeditor.component.ModuleWindow;
import org.minecraft.wise.api.gui.hudeditor.component.SettingComponent;
import org.minecraft.wise.api.gui.hudeditor.component.settings.*;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.render.animations.Easing;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.custom.Bind;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ModuleComponent implements IMinecraft, Component {

    private static final float HEIGHT = 15.0F;

    private static final float WIDTH = 100.0F;

    private final Feature module;

    private final ModuleWindow parent;

    private boolean opened;

    private boolean stillMoving;

    private final List<SettingComponent<?>> settingComponents = new ArrayList<>();

    private float y;

    private long openTime;

    private float currentAnimationProgress;

    @SuppressWarnings({"unchecked"})
    public ModuleComponent(Feature module, ModuleWindow parent) {
        this.module = module;
        this.parent = parent;
        for (Value<?> setting : module.getValues()) {
            if (setting.getValue() instanceof Boolean) {
                this.settingComponents.add(new BooleanComponent((Value<Boolean>) setting, this));
            } else if (setting.getValue() instanceof String) {
                if (!setting.getName().equals("Name")) {
                    this.settingComponents.add(new EnumComponent((Value<String>) setting, this));
                }
            } else if (setting.getValue() instanceof Number) {
                this.settingComponents.add(new NumberComponent((Value<Number>) setting, this));
            } else if (setting.getValue() instanceof Bind) {
                this.settingComponents.add(new BindComponent((Value<Bind>) setting, this));
            } else if (setting.getValue() instanceof Color) {
                this.settingComponents.add(new ColorComponent((Value<Color>) setting, this));
            }
        }
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Color disabled = new Color(35 / 255.0F, 35 / 255.0F, 35 / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
//        Color syncTopLeft = this.module.isEnabled() ? Colors.getInstance().getSyncedColor(this.getX(), this.getY(), ClientGuiPort.getInstance().getColorTransparency()) : disabled;
//        Color syncBottomLeft = this.module.isEnabled() ? Colors.getInstance().getSyncedColor(this.getX(), this.getY() + HEIGHT, ClientGuiPort.getInstance().getColorTransparency()) : disabled;
//        Color syncBottomRight = this.module.isEnabled() ? Colors.getInstance().getSyncedColor(this.getX() + WIDTH, this.getY() + HEIGHT, ClientGuiPort.getInstance().getColorTransparency()) : disabled;
//        Color syncTopRight = this.module.isEnabled() ? Colors.getInstance().getSyncedColor(this.getX() + WIDTH, this.getY(), ClientGuiPort.getInstance().getColorTransparency()) : disabled;
//
//        if (this.isMouseOverThis(mouseX, mouseY)) {
//            syncTopLeft = syncTopLeft.brighter();
//            syncBottomLeft = syncBottomLeft.brighter();
//            syncBottomRight = syncBottomRight.brighter();
//            syncTopRight = syncTopRight.brighter();
//        }

        RenderUtils.drawRect(context.getMatrices(), this.parent.getX(), this.y, WIDTH, HEIGHT, module.isEnabled() ? HudColors.INSTANCE.mainColor.getValue().getRGB() : disabled.getRGB());
        Color textColor = this.module.isEnabled() ? new Color(240, 240, 240) : new Color(120, 120, 120);
        textColor = new Color(textColor.getRed() / 255.0F, textColor.getGreen() / 255.0F, textColor.getBlue() / 255.0F, HudGuiPort.getInstance().getColorTransparency() / 255.0F);
        FontManager.drawText(context, this.module.getName(), (int) (this.parent.getX() + 2), (int) (this.y + 4), ColorUtil.getRGBA(textColor));

        float currY = 0.0F;
        float targetY = 0.0F;
        float diff = System.currentTimeMillis() - this.openTime;
        for (SettingComponent<?> settingComponent : this.settingComponents) {
            targetY += settingComponent.getHeight();
        }
        if (this.opened) {
            this.currentAnimationProgress = Easing.exponential(diff, 0.0F, targetY, 150L);
            if (this.currentAnimationProgress < targetY) {
                this.stillMoving = true;
                RenderUtils.prepareScissor(context, (int) this.getX(), (int) (this.getY() + HEIGHT), (int) (this.getX() + WIDTH), (int) (this.getY() + HEIGHT + this.getHeight()));
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            } else {
                this.stillMoving = false;
            }
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.setY(this.y + (currY += settingComponent.getHeight()));
                settingComponent.setY(settingComponent.getY() - targetY);
                settingComponent.setY(settingComponent.getY() + this.currentAnimationProgress);
                settingComponent.drawScreen(context, mouseX, mouseY, partialTicks);
            }
            if (this.stillMoving) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        } else {
            if (this.currentAnimationProgress > 0.0F) {
                RenderUtils.prepareScissor(context, (int) this.getX(), (int) (this.getY() + HEIGHT), (int) (this.getX() + WIDTH), (int) (this.getY() + HEIGHT + this.getHeight()));
                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                float reverseProgress = Easing.exponential(diff, 0.0F, targetY, 150L);
                this.currentAnimationProgress = targetY - reverseProgress;
                for (SettingComponent<?> settingComponent : this.settingComponents) {
                    settingComponent.setY(this.y + (currY += settingComponent.getHeight()));
                    settingComponent.setY(settingComponent.getY() - reverseProgress);
                    settingComponent.drawScreen(context, mouseX, mouseY, partialTicks);
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
//                GL11.glPopAttrib();
            }
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.module.toggle();
        }
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.onLeftClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRightClick(int mouseX, int mouseY) {
        if (this.isMouseOverThis(mouseX, mouseY)) {
            this.playClickSound();
            this.opened = !this.opened;
            this.openTime = System.currentTimeMillis();
        }
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.onRightClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onLeftRelease(int mouseX, int mouseY) {
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.onLeftRelease(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRightRelease(int mouseX, int mouseY) {
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.onRightRelease(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onMiddleClick(int mouseX, int mouseY) {
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.onMiddleClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.opened && !this.stillMoving) {
            for (SettingComponent<?> settingComponent : this.settingComponents) {
                settingComponent.keyTyped(typedChar, keyCode);
            }
        }
    }

    public boolean isMouseOverThis(float mouseX, float mouseY) {
        return mouseX >= this.parent.getX() && mouseX <= this.parent.getX() + WIDTH && mouseY > this.y && mouseY <= this.y + HEIGHT;
    }

    public Feature getModule() {
        return this.module;
    }

    public List<SettingComponent<?>> getSettingComponents() {
        return this.settingComponents;
    }

    public float getX() {
        return this.parent.getX();
    }

    public float getY() {
        return this.y;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public float getHeight() {
        return HEIGHT + this.currentAnimationProgress;
    }

    public void setY(float y) {
        this.y = y;
    }
}