package org.minecraft.wise.impl.features.hud;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render2dEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.hud.HudComponent;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.management.TPSManager;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.math.FramerateCounter;
import org.minecraft.wise.api.utils.math.MathUtil;
import org.minecraft.wise.api.utils.render.animations.Animation;
import org.minecraft.wise.api.utils.render.animations.Easings;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.features.modules.client.HudColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.minecraft.wise.impl.features.modules.client.HudEditor;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Info extends HudComponent {
    final Value<Boolean> potion = new ValueBuilder<Boolean>().withDescriptor("Potions").withValue(false).register(this);
    final Value<Boolean> potionLogos = new ValueBuilder<Boolean>().withDescriptor("Potion Logos").withValue(true).register(this);
    final Value<Boolean> Brackets = new ValueBuilder<Boolean>().withDescriptor("Brackets").withValue(true).register(this);
    final Value<Boolean> TPS = new ValueBuilder<Boolean>().withDescriptor("TPS").withValue(true).register(this);
    final Value<Boolean> advTPS = new ValueBuilder<Boolean>().withDescriptor("AdvTPS").withValue(true).register(this);
    final Value<Boolean> FPS = new ValueBuilder<Boolean>().withDescriptor("FPS").withValue(true).register(this);
    final Value<Boolean> ping = new ValueBuilder<Boolean>().withDescriptor("Ping").withValue(true).register(this);
    final Value<Boolean> serverBrand = new ValueBuilder<Boolean>().withDescriptor("Server Brand").withValue(true).register(this);
    final Value<Boolean> Speed = new ValueBuilder<Boolean>().withDescriptor("Speed").withValue(true).register(this);
    final Value<Boolean> packets = new ValueBuilder<Boolean>().withDescriptor("Packets").withValue(true).register(this);
    final Value<Boolean> ColoredTps = new ValueBuilder<Boolean>().withDescriptor("ColoredTps").withValue(true).register(this);
    final Value<Boolean> Durability = new ValueBuilder<Boolean>().withDescriptor("Durability").withValue(false).register(this);
    int packetsSent = 0;
    int packetsReceived = 0;
    int off = 0;
    private final Timer timer = new Timer.Single();
    private final Animation animation = new Animation(Easings.EASE_OUT_QUAD, 150);


    public Info() {
        super("Info");
    }

    public static double coordsDiff(char s) {
        return switch (s) {
            case 'x' -> mc.player.getX() - mc.player.prevX;
            case 'z' -> mc.player.getZ() - mc.player.prevZ;
            default -> 0.0;
        };
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.time == PacketEvent.Time.Send) {
            packetsSent++;
        }

        if (event.time == PacketEvent.Time.Receive) {
            packetsReceived++;
        }

    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (timer.hasPassed(1000)) {
            packetsReceived = 0;
            packetsSent = 0;
            timer.reset();
        }
    }

    @Subscribe
    public void draw(Render2dEvent event) {
        DecimalFormat minuteFormatter = new DecimalFormat("0");
        DecimalFormat secondsFormatter = new DecimalFormat("00");

        ArrayList<InfoComponent> potions = new ArrayList<>();
        ArrayList<InfoComponent> info = new ArrayList<>();

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            double timeS = (double) effect.getDuration() / 20 % 60;
            double timeM = (double) effect.getDuration() / 20 / 60;
            String time = minuteFormatter.format(timeM) + ":" + secondsFormatter.format(timeS);
            String name;

            if (effect.getDuration() > 30000) {
                time = "**:**";
            }

            if (effect.getAmplifier() == 0) {
                name = Brackets.getValue()
                        ? effect.getEffectType().value().getName().getString() + " " + Formatting.GRAY + "[" + Formatting.WHITE + time + Formatting.GRAY + "]"
                        : effect.getEffectType().value().getName().getString() + " " + Formatting.WHITE + time;
            } else {
                name = Brackets.getValue()
                        ? effect.getEffectType().value().getName().getString() + " " + (effect.getAmplifier() + 1) + " " + Formatting.GRAY + "[" + Formatting.WHITE + time + Formatting.GRAY + "]"
                        : effect.getEffectType().value().getName().getString() + " " + (effect.getAmplifier() + 1) + " " + Formatting.WHITE + time;
            }

            potions.add(new InfoComponent(new Color(effect.getEffectType().value().getColor()), name));
        }

        if (Durability.getValue()) {
            ItemStack currentItem = mc.player.getMainHandStack();

            if (currentItem.isDamageable()) {
                float green = ((float) currentItem.getMaxDamage() - (float) currentItem.getDamage()) / (float) currentItem.getMaxDamage();
                float red = 1.0f - green;
                int dmg = 100 - (int) (red * 100.0f);

                info.add(new InfoComponent(null, "Durability " + Formatting.WHITE + dmg));
            }
        }

        if (Speed.getValue()) {
            info.add(new InfoComponent(null, "Speed " + Formatting.WHITE + String.format("%.2f", (double) MathHelper.sqrt((float) (Math.pow(coordsDiff('x'), 2.0) + Math.pow(coordsDiff('z'), 2.0))) / 0.05 * 3.6) + " km/h"));
        }

        if (FPS.getValue()) {
            FramerateCounter.INSTANCE.recordFrame();
            info.add(new InfoComponent(null, "FPS " + Formatting.WHITE + FramerateCounter.INSTANCE.getFps()));
        }

        if (TPS.getValue()) {
            String string = "TPS " + Formatting.WHITE + MathUtil.roundFloat(TPSManager.INSTANCE.getTickRate(), 2);

            if (advTPS.getValue()) {
                string += "*" + Formatting.DARK_GRAY + " [" + Formatting.WHITE + getTpsColor(TPSManager.INSTANCE.getTickRate()) + MathUtil.roundFloat(TPSManager.INSTANCE.getAverage(), 2) + Formatting.DARK_GRAY + "]";
            }
            info.add(new InfoComponent(null, string));
        }

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

        if (ping.getValue() && playerListEntry != null) {
            info.add(new InfoComponent(null, "Ping " + Formatting.WHITE + getPlayerLatency(mc.player)));
        }

        if (packets.getValue()) {
            info.add(new InfoComponent(null, "Packets " + Formatting.WHITE + packetsSent + Formatting.GRAY + " [" + Formatting.WHITE + packetsReceived + Formatting.GRAY +  "]"));
        }

        info.sort(Comparator.comparingInt(i2 -> -FontManager.getWidth(i2.text)));
        potions.sort(Comparator.comparingInt(i2 -> -FontManager.getWidth(i2.text)));
        off = 0;

        if (serverBrand.getValue()) {
            int k2 = mc.currentScreen instanceof ChatScreen ? 14 : 0;
            int x2 = !autoPos.getValue() ? xPos.getValue().intValue() - FontManager.getWidth(mc.isInSingleplayer() ? "Singleplayer (Integrated)" : mc.getNetworkHandler().getBrand()) + width : event.getContext().getScaledWindowWidth() - FontManager.getWidth( mc.isInSingleplayer() ? "Singleplayer (Integrated)" : mc.getNetworkHandler().getBrand()) - 2;

            FontManager.drawText(event.getContext(), mc.isInSingleplayer() ? "Singleplayer (Integrated)" : mc.getNetworkHandler().getBrand(),
                    x2 - 1,
                    yPos.getValue().intValue() - off + 1 - k2,
                    HudColors.getTextColor(yPos.getValue().intValue() + off).getRGB());
            off += 9;
        }

        if (potion.getValue()) {
            renderPotions(event.getContext(), potions);
        }


        renderInfo(event.getContext(), info);

        if (autoPos.getValue()) {
            yPos.setValue(event.getContext().getScaledWindowHeight() - 10);
        }

        animation.run(mc.currentScreen instanceof ChatScreen ? 14 : 0);
    }

    public void renderPotions(DrawContext context, ArrayList<InfoComponent> potions) {

        List<StatusEffectInstance> activeEffects = new ArrayList<>(mc.player.getStatusEffects());

        int maxPotions = Math.min(potions.size(), activeEffects.size());

        for (int i = 0; i < maxPotions; i++) {
            InfoComponent comp = potions.get(i);
            StatusEffectInstance effect = activeEffects.get(i);
            int x2 = !autoPos.getValue()
                    ? xPos.getValue().intValue() - FontManager.getWidth(comp.text) + width
                    : context.getScaledWindowWidth() - FontManager.getWidth(comp.text) - 2;
            FontManager.drawText(context, comp.text, x2 - 1, (int) (yPos.getValue().intValue() - off + 1 - animation.getValue()), comp.color.getRGB());


            if (potionLogos.getValue()) {
                context.getMatrices().push();
                context.getMatrices().translate(x2 - 10, yPos.getValue().intValue() - off + 1 - animation.getValue(), 0);
                context.drawSprite(0, 0, 0, 9, 9, mc.getStatusEffectSpriteManager().getSprite(effect.getEffectType()));
                context.getMatrices().pop();
            }

            off += FontManager.getHeight(comp.text);
        }
    }

    public void renderInfo(DrawContext context, ArrayList<InfoComponent> info) {

        for (InfoComponent comp : info) {
            int x2 = !autoPos.getValue() ? xPos.getValue().intValue() - FontManager.getWidth(comp.text) + width : context.getScaledWindowWidth() - FontManager.getWidth(comp.text) - 2;

            FontManager.drawText(context,
                    HudEditor.INSTANCE.lowercase.getValue() ? comp.text.toLowerCase() : comp.text,
                    x2 - 1,
                    (int) (yPos.getValue().intValue() - off + 1 - animation.getValue()),
                    HudColors.getTextColor(yPos.getValue().intValue() + off).getRGB());

            off += FontManager.getHeight(comp.text) + 1;
        }
    }

    public Formatting getTpsColor(double tps) {
        if (ColoredTps.getValue()) {
            if (tps >= 18.0) {
                return Formatting.GREEN;
            }
            if (tps >= 16.0) {
                return Formatting.DARK_GREEN;
            }
            if (tps >= 12.0) {
                return Formatting.YELLOW;
            }
            return Formatting.RED;
        }
        return Formatting.WHITE;
    }

    public static int getPlayerLatency(PlayerEntity player) {
        if (player == null) return 0;

        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        return playerListEntry == null ? 0 : playerListEntry.getLatency();
    }

    public static class InfoComponent {
        final Color color;
        final String text;

        public InfoComponent(Color color, String text) {
            this.color = color;
            this.text = text;
        }
    }

}
