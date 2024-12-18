package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoReplenish extends Module {
    public final Value<Number> threshold = new ValueBuilder<Number>().withDescriptor("Threshold").withValue(10.0f).withRange(0.0f, 64.0f).register(this);
    public final Value<Boolean> pearl = new ValueBuilder<Boolean>().withDescriptor("Pearls").withValue(false).register(this);
    public final Value<Number> pearlThreshold = new ValueBuilder<Number>().withDescriptor("PearlThreshold").withValue(10.0f).withRange(0.0f, 16.0f).register(this);

    public AutoReplenish() {
        super("AutoReplenish", Category.Misc);
        setDescription("Refills an item with a low quantity.");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.currentScreen != null)
            return;

        for (int slot = 0; slot < 9; ++slot) {
            if (replenishItemSlot(slot))
                return;
        }
    }

    private boolean replenishItemSlot(int slot) {
        PlayerInventory inventory = mc.player.getInventory();
        ItemStack stack = inventory.getStack(slot);

        if (isRefillable(stack)) {
            for (int i = 9; i < 36; ++i) {
                ItemStack itemStack = inventory.getStack(i);

                if (!itemStack.isEmpty() && areItemsEqual(stack, itemStack)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isRefillable(ItemStack stack) {
        if (stack.getItem() == Items.ENDER_PEARL && pearl.getValue()) {
            return !stack.isEmpty() && stack.getCount() <= pearlThreshold.getValue().intValue() && stack.getItem() != Items.AIR && stack.isStackable() &&
                    stack.getCount() < stack.getMaxCount();
        }

        return !stack.isEmpty() && stack.getCount() <= threshold.getValue().intValue() && stack.getItem() != Items.AIR && stack.isStackable() &&
                stack.getCount() < stack.getMaxCount();
    }

    private boolean areItemsEqual(ItemStack left, ItemStack right) {
        return left.getItem() == right.getItem() && ItemStack.areItemsEqual(left, right) && (!left.isDamageable() || left.getDamage() == right.getDamage());
    }
}
