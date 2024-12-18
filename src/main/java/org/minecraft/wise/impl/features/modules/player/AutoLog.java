package org.minecraft.wise.impl.features.modules.player;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class AutoLog extends Module {
    public final Value<Number> health = new ValueBuilder<Number>().withDescriptor("Health").withValue(10.0f).withRange(0.0f, 36.0f).register(this);
    public final Value<Boolean> healthTotems = new ValueBuilder<Boolean>().withDescriptor("HealthTotems").withValue(false).register(this);
    public final Value<Boolean> totems = new ValueBuilder<Boolean>().withDescriptor("Totems").withValue(false).register(this);
    public final Value<Number> count = new ValueBuilder<Number>().withDescriptor("Count").withValue(1.0f).withRange(0.0f, 10.0f).register(this);

    public AutoLog() {
        super("AutoLog", Category.Player);
        setDescription("Automatically logs you out at a certain health point.");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        float shit = mc.player.getHealth() + mc.player.getAbsorptionAmount();

        if (shit <= health.getValue().intValue()) {
            if (healthTotems.getValue() && getItemCount(Items.TOTEM_OF_UNDYING) != 0)
                return;

            if (mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().getConnection().disconnect(Text.of("[AutoLog] Logged due to no health remaining."));
                setEnabled(false);
            }
        }

        if (totems.getValue()) {
            if (getItemCount(Items.TOTEM_OF_UNDYING) <= count.getValue().intValue()) {
                if (mc.getNetworkHandler() != null) {
                    mc.getNetworkHandler().getConnection().disconnect(Text.of("[AutoLog] Logged due to your totems being below the count."));
                    setEnabled(false);
                }
            }
        }
    }

    public int getItemCount(Item item) {
        if (mc.player == null) return 0;

        int counter = 0;

        for (int i = 0; i <= 44; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            counter += itemStack.getCount();
        }

        return counter;
    }
}