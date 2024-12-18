package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.render.animations.Animation;
import org.minecraft.wise.api.utils.render.animations.Easings;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.minecraft.wise.impl.features.modules.client.HudEditor;

public class Coords extends HudComponent {
    private final Value<Boolean> direction = new ValueBuilder<Boolean>().withDescriptor("Direction").withValue(false).register(this);
    private final Value<Boolean> yaw = new ValueBuilder<Boolean>().withDescriptor("Yaw").withValue(false).register(this);
    private final Animation animation = new Animation(Easings.EASE_OUT_QUAD, 150);

    public Coords() {
        super("Coords");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        if (NullUtils.nullCheck()) return;

        boolean inHell = mc.world.getRegistryKey().getValue().getPath().equals("the_nether");

        String facingText = getFacing() + Formatting.GRAY + "]";

        if (direction.getValue()) {
            FontManager.drawText(event.getContext(),
                    HudEditor.INSTANCE.lowercase.getValue() ? facingText.toLowerCase() : facingText,
                    xPos.getValue().intValue() + 1, (int) (yPos.getValue().intValue() - animation.getValue() - 10), HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
        }

        if (inHell) {
            FontManager.drawText(event.getContext(),
                        "XYZ " +
                                Formatting.WHITE +
                                String.format("%.2f", mc.player.getX()) +
                                Formatting.RESET +
                                ", " +
                                Formatting.WHITE +
                                String.format("%.2f", mc.player.getY()) +
                                Formatting.RESET +
                                ", " +
                                Formatting.WHITE +
                                String.format("%.2f", mc.player.getZ()) +
                                Formatting.GRAY +
                                " (" +
                                Formatting.WHITE +
                                String.format("%.2f", mc.player.getX() * 8.0) +
                                Formatting.RESET +
                                ", " +
                                Formatting.WHITE +
                                String.format("%.2f", mc.player.getZ() * 8.0) +
                                Formatting.GRAY +
                                ")",
                    (int) (xPos.getValue().intValue() + 1.0f),
                    (int) (yPos.getValue().intValue() - animation.getValue()),
                    HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
        } else {
            FontManager.drawText(event.getContext(), "XYZ " + Formatting.WHITE + String.format("%.2f", mc.player.getX()) + Formatting.RESET + ", " + Formatting.WHITE + String.format("%.2f", mc.player.getY()) + Formatting.RESET + ", " + Formatting.WHITE + String.format("%.2f", mc.player.getZ()) + Formatting.GRAY + " (" + Formatting.WHITE + String.format("%.2f", mc.player.getX() / 8.0) + Formatting.RESET + ", " + Formatting.WHITE + String.format("%.2f", mc.player.getZ() / 8.0) + Formatting.GRAY + ")", (int) (xPos.getValue().intValue() + 1.0f), (int) (yPos.getValue().intValue() - animation.getValue()), HudColors.getTextColor(yPos.getValue().intValue()).getRGB());
        }

        if (autoPos.getValue()) {
            xPos.setValue(1);
            yPos.setValue(event.getContext().getScaledWindowHeight() - 10);
        }
        animation.run(mc.currentScreen instanceof ChatScreen ? 14 : 0);
    }

    private String getFacing() {
        String yawString = yaw.getValue() ? (", " + Formatting.WHITE + String.format("%.0f", MathHelper.wrapDegrees(mc.player.getYaw())) + Formatting.RESET) : "";

        return switch (MathHelper.floor(mc.player.getYaw() * 4.0f / 360.0f + 0.5) & 0x3) {
            case 0 -> Formatting.WHITE + "South " + Formatting.GRAY + "[" + Formatting.WHITE + "+Z" + Formatting.GRAY + yawString;
            case 1 -> Formatting.WHITE + "West " + Formatting.GRAY + "[" + Formatting.WHITE + "-X" + Formatting.GRAY + yawString;
            case 2 -> Formatting.WHITE + "North " + Formatting.GRAY + "[" + Formatting.WHITE + "-Z" + Formatting.GRAY + yawString;
            case 3 -> Formatting.WHITE + "East " + Formatting.GRAY + "[" + Formatting.WHITE + "+X" + Formatting.GRAY + yawString;
            default -> "Invalid";
        };
    }
}
