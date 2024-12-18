package org.minecraft.wise.api.gui.hudeditor;


import net.minecraft.client.gui.DrawContext;

public interface Component {

    void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks);

    void onLeftClick(int mouseX, int mouseY);

    void onRightClick(int mouseX, int mouseY);

    void onLeftRelease(int mouseX, int mouseY);

    void onRightRelease(int mouseX, int mouseY);

    void onMiddleClick(int mouseX, int mouseY);

    void keyTyped(char typedChar, int keyCode);

    float getHeight();

    default float getLastMouseX() {
        return HudGuiPort.getInstance().getLastMouseX();
    }

    default float getLastMouseY() {
        return HudGuiPort.getInstance().getLastMouseY();
    }

    default boolean isLeftMouseHeld() {
        return HudGuiPort.getInstance().isLeftMouseHeld();
    }

    default boolean isRightMouseHeld() {
        return HudGuiPort.getInstance().isRightMouseHeld();
    }

    default void playClickSound() {
//        Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}