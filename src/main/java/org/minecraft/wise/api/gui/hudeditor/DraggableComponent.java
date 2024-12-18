package org.minecraft.wise.api.gui.hudeditor;


import org.minecraft.wise.api.wrapper.IMinecraft;

public abstract class DraggableComponent implements IMinecraft, Component {

    private float x;

    private float y;

    public DraggableComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drag(float mouseX, float mouseY) {
        this.x += mouseX - this.getLastMouseX();
        this.y += mouseY - this.getLastMouseY();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}