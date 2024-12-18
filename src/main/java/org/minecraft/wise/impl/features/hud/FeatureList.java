package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FeatureManager;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.util.Formatting;
import org.minecraft.wise.impl.features.modules.client.HudEditor;

import java.util.ArrayList;
import java.util.Comparator;

public class FeatureList extends HudComponent {
    public static FeatureList INSTANCE;
    final Value<Boolean> animations = new ValueBuilder<Boolean>().withDescriptor("Animation").withValue(false).register(this);
    final Value<Boolean> noBrackets = new ValueBuilder<Boolean>().withDescriptor("No Brackets").withValue(false).register(this);
    final Value<Boolean> noMetadata = new ValueBuilder<Boolean>().withDescriptor("No Metadata").withValue(false).register(this);

    public FeatureList() {
        super("FeatureList");
        INSTANCE = this;
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        super.draw(event);
        if (NullUtils.nullCheck())
            return;

        ArrayList<Feature> sorted = new ArrayList<>();

        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if ((!feature.visible.getValue()))
                continue;
            sorted.add(feature);
        }

        sorted.sort(Comparator.comparingInt(mod -> {
            int o2 = noBrackets.getValue() ? FontManager.getWidth(mod.getDisplayName() + (!mod.getHudInfo().isEmpty() ? Formatting.GRAY + " " + mod.getHudInfo() : "")) : FontManager.getWidth(mod.getDisplayName() + (!mod.getHudInfo().isEmpty() ? Formatting.GRAY + " [" + Formatting.WHITE + mod.getHudInfo() + Formatting.GRAY + "]" : ""));
            return -o2;
        }));

        if (autoPos.getValue()) {
            yPos.setValue(2);
        }

        int offset = 0;
        for (Feature module : sorted) {
            module.animation.run(module.isEnabled() ? 1.0F : 0.0F);
            if (!module.isEnabled() && module.animation.getValue() == 0.0F)
                continue;

            String text = module.getDisplayName() + (!module.getHudInfo().isEmpty() ? Formatting.GRAY + " [" + Formatting.WHITE + module.getHudInfo() + Formatting.GRAY + "]" : "");

            if (noBrackets.getValue()) {
                text = module.getDisplayName() + (!module.getHudInfo().isEmpty() ? Formatting.GRAY + " " + Formatting.GRAY + module.getHudInfo() : "");
            }

            module.width.run(FontManager.getWidth(HudEditor.INSTANCE.lowercase.getValue() ? text.toLowerCase() : text));

            if (animations.getValue()) {
                FontManager.drawText(event.getContext(),
                        HudEditor.INSTANCE.lowercase.getValue() ? text.toLowerCase() : text,
                        !autoPos.getValue() ? (int) (xPos.getValue().intValue() - (module.width.getValue() * module.animation.getValue()) + 2) : (int) (event.getContext().getScaledWindowWidth() - (module.width.getValue() * module.animation.getValue()) - 2),
                        !autoPos.getValue() ? yPos.getValue().intValue() + offset + 2 : 2 + offset,
                        HudColors.getTextColor(yPos.getValue().intValue() + offset).getRGB());
                offset += 10;
                continue;
            }
            
            FontManager.drawText(event.getContext(), HudEditor.INSTANCE.lowercase.getValue() ? text.toLowerCase() : text, !autoPos.getValue() ? xPos.getValue().intValue() + getWidth() - FontManager.getWidth(text) + 2 : event.getContext().getScaledWindowWidth() - FontManager.getWidth(text) - 2, !autoPos.getValue() ? yPos.getValue().intValue() + offset + 2 : 2 + offset, HudColors.getTextColor(yPos.getValue().intValue() + offset).getRGB());
            offset += 10;
        }

        height = offset;
        width = FontManager.getWidth(sorted.get(1).getDisplayName() + (!sorted.get(1).getHudInfo().isEmpty() ? Formatting.GRAY + " [" + Formatting.WHITE + sorted.get(1).getHudInfo() + Formatting.GRAY + "]" : ""));
    }
}