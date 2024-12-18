package org.minecraft.wise.api.gui.mainmenu;

import org.minecraft.GitInfo;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.WiseMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;

public class WiseMainMenu extends Screen implements IMinecraft {

    private final ArrayList<WiseButton> wiseButtons = new ArrayList<>();

    public WiseMainMenu() {
        super(Text.of("WiseMainMenu"));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        // Background
        RenderUtils.drawRect(context.getMatrices(), 0, 0, width, height, new Color(8, 8, 12));

        // Build Text
        FontManager.drawTextSmall(context, "Build: ", 2, height - 12, new Color(40, 40, 40).getRGB());
        FontManager.drawTextSmall(context,
                GitInfo.GIT_DATE.replace("Z", "").replace("T", " "),
                FontManager.getWidthSmall("Build: ") + 2,
                height - 12,
                new Color(40, 141, 200).getRGB());

        // Main watermark
        FontManager.drawTextBig(context, WiseMod.NAME.toUpperCase(),
                width / 2 - (FontManager.getWidthBig(WiseMod.NAME.toUpperCase()) / 2) + 1,
                height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 5) + 1,
                new Color(40, 141, 200).getRGB());
        FontManager.drawTextBig(context, WiseMod.NAME.toUpperCase(),
                width / 2 - (FontManager.getWidthBig(WiseMod.NAME.toUpperCase()) / 2),
                height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 5),
                -1);

        // Splash text
        FontManager.drawTextSmall(context, "made by vasler with love",
                width / 2 - (FontManager.getWidthSmall("made by vasler with love") / 2),
                height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 30),
                -1);

        // Buttons
        drawButton("Singleplayer", width / 2 - 140, height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 45), 70, 15,
                () -> mc.setScreen(new SelectWorldScreen(this)));
        drawButton("Multiplayer", width / 2 - 70, height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 45), 70, 15,
                () -> mc.setScreen(new MultiplayerScreen(this)));
        drawButton("Options", width / 2, height / 2 - (FontManager.getHeightBig(WiseMod.NAME.toUpperCase()) - 45), 70, 15,
                () -> mc.setScreen(new OptionsScreen(this, mc.options)));

        // Render buttons
        wiseButtons.forEach(button -> button.render(context, mouseX, mouseY, delta));

    }

    public void drawButton(String string, int x, int y, float width, float height, Runnable runnable) {
        wiseButtons.add(new WiseButton(string, x, y, width, height, runnable));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Click event
        wiseButtons.forEach(buttons -> buttons.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class WiseButton {

        private final int x, y;
        private final float width, height;
        private final String string;
        private final Runnable runnable;

        public WiseButton(String string, int x, int y, float width, float height, Runnable runnable) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.string = string;
            this.runnable = runnable;
        }

        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            RenderUtils.drawRect(context.getMatrices(), x, y, width, height, new Color(3, 12, 27));

            // Text
            FontManager.drawTextSmall(context, string, (x - FontManager.getWidthSmall(string) / 2), y,
                    isMouseOverThis(mouseX, mouseY) ? new Color(200, 200, 200).getRGB() : new Color(117, 126, 138).getRGB());
        }

        public void mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOverThis((float) mouseX, (float) mouseY) && button == 0) {
                runnable.run();
            }
        }

        public boolean isMouseOverThis(float mouseX, float mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY <= y + height;
        }
    }
}
