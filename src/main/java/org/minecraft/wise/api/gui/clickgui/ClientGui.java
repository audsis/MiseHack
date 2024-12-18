package org.minecraft.wise.api.gui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClientGui extends Screen {

    public static final ClientGui instance = new ClientGui();

    public ClientGui() {
        super(Text.literal("WiseGui"));
    }

    @Override
    public void init() {
        super.init();
        ClientGuiPort.getInstance().initGui();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        ClientGuiPort.getInstance().drawScreen(context, mouseX, mouseY, delta);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClientGuiPort.getInstance().mouseClicked((int) mouseX, (int) mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClientGuiPort.getInstance().mouseReleased((int) mouseX, (int) mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ClientGuiPort.getInstance().keyPressed(keyCode);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        ClientGuiPort.getInstance().keyTyped(chr, modifiers);

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        ClientGuiPort.getInstance().setY((float) (ClientGuiPort.getInstance().getY() + verticalAmount * 10f));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }



    @Override
    public void close() {
        super.close();
        ClientGuiPort.getInstance().onGuiClosed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static ClientGui getInstance() {
        return instance;
    }
}