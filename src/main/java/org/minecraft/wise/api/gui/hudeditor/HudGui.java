package org.minecraft.wise.api.gui.hudeditor;

import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.gui.hudeditor.hud.HudDisplay;
import org.minecraft.wise.api.management.FeatureManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class HudGui extends Screen {

    public static final HudGui instance = new HudGui();
    private final List<HudDisplay> displays;

    public HudGui() {
        super(Text.literal("HudGui"));
        displays = new CopyOnWriteArrayList<>();

        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if (feature.getCategory() != Feature.Category.Hud) continue;
            HudComponent hudModule = (HudComponent) feature;

            displays.add(new HudDisplay(hudModule.xPos.getValue().intValue(), hudModule.yPos.getValue().intValue(), hudModule.getWidth(), hudModule.getHeight(), hudModule));
        }
    }

    @Override
    public void init() {
        super.init();
        HudGuiPort.getInstance().initGui();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        HudGuiPort.getInstance().drawScreen(context, mouseX, mouseY, delta);
        displays.forEach(panel -> panel.drawScreen(context, mouseX, mouseY, delta));
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        HudGuiPort.getInstance().mouseClicked((int) mouseX, (int) mouseY, button);
        switch (button) {
            case 0:
                displays.forEach(panel -> panel.onLeftClick((int) mouseX, (int) mouseY));
            case 1:
                displays.forEach(panel -> panel.onRightClick((int) mouseX, (int) mouseY));
            case 2:
                displays.forEach(panel -> panel.onMiddleClick((int) mouseX, (int) mouseY));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        HudGuiPort.getInstance().mouseReleased((int) mouseX, (int) mouseY, button);
        switch (button) {
            case 0:
                displays.forEach(panel -> panel.onLeftRelease((int) mouseX, (int) mouseY));
            case 1:
                displays.forEach(panel -> panel.onRightRelease((int) mouseX, (int) mouseY));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        HudGuiPort.getInstance().keyPressed(keyCode);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        HudGuiPort.getInstance().keyTyped(chr, modifiers);

        return super.charTyped(chr, modifiers);
    }


    @Override
    public void close() {
        super.close();
        HudGuiPort.getInstance().onGuiClosed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static HudGui getInstance() {
        return instance;
    }
}