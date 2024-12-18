package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ArmorHud extends HudComponent {

    public ArmorHud() {
        super("ArmorHud");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        super.draw(event);
        if (NullUtils.nullCheck()) {
            return;
        }
        renderArmorHUD(true, event.getContext());
    }

    public void renderArmorHUD(boolean percent, DrawContext context) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int i2 = width / 2;
        int iteration = 0;
        int y2 = height - 55 - (mc.player.isSubmergedInWater() && !mc.player.isCreative() ? 10 : 0);
        for (ItemStack is : mc.player.getInventory().armor) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x2 = i2 - 90 + (9 - iteration) * 20 + 2;

            context.drawItem(is, x2, y2);
            context.drawItemInSlot(mc.textRenderer, is, x2, y2);

            String s2 = is.getCount() > 1 ? is.getCount() + "" : "";

            FontManager.drawText(context, s2, x2 + 19 - 2 - FontManager.getWidth(s2), y2 + 9, HudColors.getTextColor(y2 + 9).getRGB());
            if (!percent) continue;

            float green = ((float) is.getMaxDamage() - (float) is.getDamage()) / (float) is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int) (red * 100.0f);

            context.getMatrices().push();
            context.getMatrices().scale(0.75F, 0.75F, 1F);
            FontManager.drawText(context, dmg + "%", (int) ((int) (x2 + 8 - (float) FontManager.getWidth(dmg + "") / 2) * 1.333F), (int) ((y2 - 5) * 1.333F), HudColors.getTextColor(y2 + 9).getRGB());
            context.getMatrices().scale(1.0F, 1.0F, 1.0F);
            context.getMatrices().pop();
        }
    }
}