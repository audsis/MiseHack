package org.minecraft.wise.api.gui.hudeditor.component;


import org.minecraft.wise.api.gui.hudeditor.Component;
import org.minecraft.wise.api.gui.hudeditor.component.module.ModuleComponent;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.wrapper.IMinecraft;

public abstract class SettingComponent<T> implements IMinecraft, Component {

    private final Value<T> setting;

    private final ModuleComponent parent;

    private float y;

    public SettingComponent(Value<T> setting, ModuleComponent parent) {
        this.setting = setting;
        this.parent = parent;
    }

    public boolean isMouseOverThis(float mouseX, float mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + getWidth() && mouseY > this.getY() && mouseY <= this.getY() + getHeight();
    }

    public float getHeight() {
        return 16.0F;
    }

    public static float getWidth() {
        return 100.0F;
    }

    public Value<T> getSetting() {
        return this.setting;
    }

    public float getX() {
        return this.parent.getX();
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}