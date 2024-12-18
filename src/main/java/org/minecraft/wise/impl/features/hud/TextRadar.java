package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import java.awt.*;

public class TextRadar extends HudComponent {
    private final Value<Boolean> health = new ValueBuilder<Boolean>().withDescriptor("Health").withValue(true).register(this);
    private final Value<Boolean> distance = new ValueBuilder<Boolean>().withDescriptor("Distance").withValue(true).register(this);
    private final Value<Boolean> pops = new ValueBuilder<Boolean>().withDescriptor("Pops").withValue(true).register(this);
    private final Value<Boolean> self = new ValueBuilder<Boolean>().withDescriptor("Self").withValue(false).register(this);
    public final Value<Color> friendColor = new ValueBuilder<Color>().withDescriptor("Friend Color").withValue(new Color(0, 150, 255)).register(this);
    public final Value<Color> selfColor = new ValueBuilder<Color>().withDescriptor("Self Color").withValue(new Color(255, 255, 255)).register(this);

    public TextRadar() {
        super("TextRadar");
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        int offset = 0;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player) {
                if (player.isDead())
                    continue;

                if (player == mc.player && !self.getValue())
                    continue;

                String string = player.getName().getString();

                if (distance.getValue()) {
                    string = getDistanceColor((int) mc.player.distanceTo(player)) +
                            (String.format("%.0f", mc.player.distanceTo(player)) + "m ") +
                            Formatting.RESET +
                            player.getName().getString() + " ";
                }

                if (health.getValue()) {
                    int health = (int) (player.getHealth() + player.getAbsorptionAmount());
                    string += getHealthColor(health) + (health + " ");
                }

                FontManager.drawText(event.getContext(),
                        string,
                        xPos.getValue().intValue(),
                        yPos.getValue().intValue() + offset,
                        getNameColor(player, yPos.getValue().intValue() + offset).getRGB());
                offset += FontManager.getHeight(string) + 1;
            }
        }
        height = offset;
    }

    public Color getNameColor(PlayerEntity player, int offset) {
        if (player == mc.player)
            return selfColor.getValue();

        if (FriendManager.INSTANCE.isFriend(player))
            return friendColor.getValue();

        return HudColors.getTextColor(offset);
    }

    public Formatting getDistanceColor(int distance) {
        if (distance <= 10) {
            return Formatting.RED;
        } else if (distance <= 15) {
            return Formatting.GOLD;
        } else if (distance <= 20) {
            return Formatting.YELLOW;
        } else if (distance <= 25) {
            return Formatting.DARK_GREEN;
        } else {
            return Formatting.GREEN;
        }
    }

    private Formatting getHealthColor(int health) {
        if (health > 18) {
            return Formatting.GREEN;
        } else if (health > 16) {
            return Formatting.DARK_GREEN;
        } else if (health > 12) {
            return Formatting.YELLOW;
        } else if (health > 8) {
            return Formatting.GOLD;
        } else if (health > 5) {
            return Formatting.RED;
        } else {
            return Formatting.DARK_RED;
        }
    }
}
