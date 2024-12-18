package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.Manager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class Warner extends Module {

    public static Warner INSTANCE;
    private final Value<Boolean> logs = new ValueBuilder<Boolean>().withDescriptor("Logs").withValue(true).register(this);
    private final Value<Boolean> pops = new ValueBuilder<Boolean>().withDescriptor("Pops").withValue(true).register(this);
    private final Value<Boolean> self = new ValueBuilder<Boolean>().withDescriptor("Self").withValue(true).register(this);
    private final Value<Boolean> visualRange = new ValueBuilder<Boolean>().withDescriptor("VisualRange").withValue(true).register(this);
    private final Value<Boolean> potions = new ValueBuilder<Boolean>().withDescriptor("Potions").withValue(true).register(this);
    public final Object2IntOpenHashMap<String> registry = new Object2IntOpenHashMap<>();

    public Warner() {
        super("Warner", Category.Misc);
        INSTANCE = this;
    }

    public void onPop(Entity entity) {
        if (entity != null && entity.isAlive()) {

            registry.put(entity.getName().getString(), registry.getInt(entity.getName().getString()) + 1);
            int pops = registry.getInt(entity.getName().getString());

            String name = entity.getName().getString();
            if (self.getValue())
                name = entity != mc.player ? entity.getName().getString() + "'s" : "Your";

            if (this.pops.getValue()) {
                ChatUtils.sendMessage(new ChatMessage(
                        Formatting.BOLD +
                                name +
                                Formatting.GRAY +
                                " " +
                                pops +
                                appendSuffix(pops) +
                                Formatting.WHITE +
                                " totem has popped" +
                                Formatting.GRAY + ".", true, -entity.getId()));

                if (Manager.INSTANCE.notifications.getValue()) {
                    WiseMod.notificationProcessor.addNotification(Formatting.RESET +
                            name + Formatting.WHITE + " " + pops + appendSuffix(pops) + " totem has popped" + Formatting.RESET + ".", 3000L);
                }
            }
        }
    }


    @Subscribe
    public void onTick(TickEvent event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.deathTime > 0 || player.getHealth() <= 0) {
                String name = player.getName().getString();

                if (registry.containsKey(name)) {
                    int pops = registry.getInt(name);
                    registry.removeInt(name);

                    if (this.pops.getValue()) {
                        ChatUtils.sendMessage(new ChatMessage(
                                Formatting.BOLD +
                                        name +
                                        Formatting.RESET +
                                        Formatting.WHITE +
                                        " has died after popping" +
                                        (pops == 0 ? "." : " their " + Formatting.GRAY + pops + appendSuffix(pops) + Formatting.WHITE + " totem" +
                                                Formatting.GRAY + "."),
                                true, -player.getId()));

                        if (Manager.INSTANCE.notifications.getValue()) {
                            WiseMod.notificationProcessor.addNotification(Formatting.RESET +
                                    name +
                                    Formatting.WHITE +
                                    " has died after popping " +
                                    (pops == 0 ? "." : " their " + pops + appendSuffix(pops) + Formatting.WHITE + " totem" + Formatting.RESET + "."), 3000L);
                        }
                    }
                }
            }
        }
    }

    public String appendSuffix(int number) {
        if (number == 1) {
            return "st";
        }
        if (number == 2) {
            return "nd";
        }
        if (number == 3) {
            return "rd";
        }
        return "th";
    }

}
