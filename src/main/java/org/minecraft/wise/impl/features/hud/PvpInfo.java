package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.entity.TargetUtils;
import org.minecraft.wise.api.utils.player.InventoryUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import org.minecraft.wise.impl.features.modules.combat.CrystalAura;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.awt.*;

public class PvpInfo extends HudComponent {

    private final Value<String> name = new ValueBuilder<String>().withDescriptor("ClientName").withValue("wisegod.cc").register(this);
    int off = 0;

    public PvpInfo() {
        super("PvpInfo");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        if (NullUtils.nullCheck()) return;
        off = 0;

        FontManager.drawText(event.getContext(),
                name.getValue(),
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
        off += 9;

        FontManager.drawText(event.getContext(),
                "HTR",
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                getHitRangeColor().getRGB());
        off += 9;

        FontManager.drawText(event.getContext(),
                "PLR",
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                getPlaceRangeColor().getRGB());
        off += 9;

        FontManager.drawText(event.getContext(),
                String.valueOf(InventoryUtils.getItemCount(Items.TOTEM_OF_UNDYING)),
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                getTotemColor(InventoryUtils.getItemCount(Items.TOTEM_OF_UNDYING)).getRGB());
        off += 9;

        FontManager.drawText(event.getContext(),
                "PING " + getPlayerLatency(mc.player),
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                getPlayerLatency(mc.player) < 80 ? new Color(0, 255, 0).getRGB() : new Color(255, 100, 100).getRGB());
        off += 9;

        FontManager.drawText(event.getContext(),
                "LBY",
                xPos.getValue().intValue(),
                yPos.getValue().intValue() + off,
                new Color(255, 100, 100).getRGB());
        off += 9;

        height = off;
        width = FontManager.getWidth(name.getValue());
    }


    private Color getHitRangeColor() {
        LivingEntity entity = TargetUtils.getTarget(CrystalAura.INSTANCE.range.getValue().intValue());
        if (CrystalAura.INSTANCE.isEnabled() && entity != null && mc.player.distanceTo(entity) < CrystalAura.INSTANCE.range.getValue().intValue()) {
            return Color.GREEN;
        } else {
            return new Color(255, 100, 100);
        }
    }

    private Color getPlaceRangeColor() {
        LivingEntity entity = TargetUtils.getTarget(CrystalAura.INSTANCE.range.getValue().intValue());
        if (CrystalAura.INSTANCE.isEnabled() && entity != null && mc.player.distanceTo(entity) < CrystalAura.INSTANCE.range.getValue().intValue()) {
            return Color.GREEN;
        } else {
            return new Color(255, 100, 100);
        }
    }

    public static int getPlayerLatency(PlayerEntity player) {
        if (player == null) return 0;

        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        return playerListEntry == null ? 0 : playerListEntry.getLatency();
    }


    private Color getTotemColor(int totemCount) {

        if (totemCount <= 0) return new Color(255, 100, 100);
        if (totemCount == 1) return new Color(50, 255, 255);
        if (totemCount == 2) return new Color(50, 150, 255);
        if (totemCount == 3) return new Color(100, 100, 255);
        if (totemCount == 4) return new Color(50, 50, 255);

        return new Color(0, 0, 255);
    }
}
