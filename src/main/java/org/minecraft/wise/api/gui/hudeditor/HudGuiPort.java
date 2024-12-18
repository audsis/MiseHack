package org.minecraft.wise.api.gui.hudeditor;

import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.gui.hudeditor.component.ModuleWindow;
import org.minecraft.wise.api.utils.render.animations.Easing;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.ClickGUI;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class HudGuiPort implements IMinecraft {

    private static final HudGuiPort instance = new HudGuiPort();

    private float lastMouseX;

    private float lastMouseY;

    private boolean leftMouseHeld;

    private boolean rightMouseHeld;

    private ModuleWindow focusedWindow;

    private ModuleWindow draggingWindow;

    private final List<ModuleWindow> windows = new ArrayList<>();

    private long openTime = 0L;

    private boolean guiClosed = false;

    private HudGuiPort() {
        float x = 15.0F - 110.0F;
        for (Feature.Category category : Feature.Category.values()) {
            if (category.equals(Feature.Category.Hud)) {
                windows.add(new ModuleWindow(x += 105.0F, 15.0F, category));
            }
        }
    }

    public void initGui() {
        this.guiClosed = false;
        this.openTime = System.currentTimeMillis();
    }

    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (this.guiClosed && (System.currentTimeMillis() - this.openTime >= 250L)) {
            mc.setScreen(null);
        }

        this.focusedWindow = null;
        for (ModuleWindow window : this.windows) {
            if (window.isMouseOverThisTab(mouseX, mouseY) && window.isOpened()) {
                this.focusedWindow = window;
                break;
            }
        }

        context.getMatrices().push();
        renderAndScaleGUI(context);

        if (draggingWindow != null) {
            this.draggingWindow.drag(mouseX, mouseY);
        }

        for (ModuleWindow window : this.windows) {
            window.drawScreen(context, mouseX, mouseY, partialTicks);
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        context.getMatrices().pop();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        switch (mouseButton) {
            case 0:
                if (this.focusedWindow != null) {
                    this.focusedWindow.onLeftClick(mouseX, mouseY);
                } else {
                    for (ModuleWindow window : this.windows) {
                        window.onLeftClick(mouseX, mouseY);
                    }
                }
                this.leftMouseHeld = true;
                break;
            case 1:
                if (this.focusedWindow != null) {
                    this.focusedWindow.onRightClick(mouseX, mouseY);
                } else {
                    for (ModuleWindow window : this.windows) {
                        window.onRightClick(mouseX, mouseY);
                    }
                }
                this.rightMouseHeld = true;
                break;
            case 2:
                if (this.focusedWindow != null) {
                    this.focusedWindow.onMiddleClick(mouseX, mouseY);
                } else {
                    for (ModuleWindow window : this.windows) {
                        window.onMiddleClick(mouseX, mouseY);
                    }
                }
                break;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        switch (button) {
            case 0:
                if (this.focusedWindow != null) {
                    this.focusedWindow.onLeftRelease(mouseX, mouseY);
                } else {
                    for (ModuleWindow window : this.windows) {
                        window.onLeftRelease(mouseX, mouseY);
                    }
                }
                this.leftMouseHeld = false;
                break;
            case 1:
                if (this.focusedWindow != null) {
                    this.focusedWindow.onRightRelease(mouseX, mouseY);
                } else {
                    for (ModuleWindow window : this.windows) {
                        window.onRightRelease(mouseX, mouseY);
                    }
                }
                this.rightMouseHeld = false;
                break;
        }
    }

    public void keyTyped(char chr, int modifiers) {
        if (this.focusedWindow != null) {
            this.focusedWindow.keyTyped(chr, modifiers);
        } else {
            for (ModuleWindow window : this.windows) {
                window.keyTyped(chr, modifiers);
            }
        }
    }

    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE && !this.guiClosed) {
            ClickGUI clickGui = ClickGUI.INSTANCE;
            if (clickGui.closeAnimation.getValue()) {
                this.openTime = System.currentTimeMillis();
                this.guiClosed = true;
            } else {
                mc.setScreen(null);
            }
        }
    }


    public void onGuiClosed() {
        ClickGUI clickGui = ClickGUI.INSTANCE;
        clickGui.toggle();
        this.draggingWindow = null;
        this.focusedWindow = null;
    }

    private void renderAndScaleGUI(DrawContext context) {
        float passedTime = System.currentTimeMillis() - this.openTime;

        float currentScale;

        if (this.guiClosed) {
            currentScale = Easing.exponential(passedTime, 0.0F, 0.25F, 250L);

            context.getMatrices().translate(context.getScaledWindowHeight(), context.getScaledWindowHeight() / 2.0F, 0.0F);
            context.getMatrices().scale(1.0F - currentScale, 1.0F - currentScale, 0.0F);
        } else {
            currentScale = Easing.exponential(passedTime, 0.75F, 0.25F, 500L);

            context.getMatrices().translate(context.getScaledWindowHeight(), context.getScaledWindowHeight()/ 2.0F, 0.0F);
            context.getMatrices().scale(currentScale, currentScale, 0.0F);
        }
        context.getMatrices().translate(-context.getScaledWindowHeight(), -context.getScaledWindowHeight() / 2.0F, 0.0F);
    }

    public float getColorTransparency() {
        float passedTime = System.currentTimeMillis() - this.openTime;
        float initialTransparency = 0.0F;
        float goalTransparency = 255.0F;
        if (this.guiClosed) {
            float alpha = 255.0F - Easing.linear(passedTime, initialTransparency, goalTransparency, 200L);
            return Math.max(alpha, 5.0F);
        }
        float alpha = Easing.linear(passedTime, initialTransparency, goalTransparency, 250L);
        return Math.max(5.0F, alpha);
    }

    public float getLastMouseX() {
        return this.lastMouseX;
    }

    public float getLastMouseY() {
        return this.lastMouseY;
    }

    public boolean isLeftMouseHeld() {
        return this.leftMouseHeld;
    }

    public boolean isRightMouseHeld() {
        return this.rightMouseHeld;
    }

    public ModuleWindow getDraggingWindow() {
        return this.draggingWindow;
    }

    public void setDraggingWindow(ModuleWindow draggingWindow) {
        this.draggingWindow = draggingWindow;
    }

    public long getOpenTime() {
        return this.openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public boolean isGuiClosed() {
        return this.guiClosed;
    }

    public void setGuiClosed(boolean guiClosed) {
        this.guiClosed = guiClosed;
    }

    public static HudGuiPort getInstance() {
        return instance;
    }
}
