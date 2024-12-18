package org.minecraft.wise.api.utils.player;

import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryUtils implements IMinecraft {

    public static void fastOffhand(int slot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, mc.options.swapHandsKey.getDefaultKey().getCode(), SlotActionType.PICKUP, mc.player);
    }

    public static void offhand(int slot) {
        int returnSlot = 0;

        if (slot == -1)
            return;

        mc.interactionManager.clickSlot(0, (slot < 9) ? (slot + 36) : slot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);

        for (int i = 0; i < 45; ++i) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                returnSlot = i;
                break;
            }
        }

        mc.interactionManager.clickSlot(0, (returnSlot < 9) ? (returnSlot + 36) : returnSlot, 0, SlotActionType.PICKUP, mc.player);
    }

    public static int getItemCount(Item item) {
        if (mc.player == null) return 0;

        int counter = 0;

        for (int i = 0; i <= 44; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            counter += itemStack.getCount();
        }

        return counter;
    }


    public static int getHotbarItemSlot(Item item) {
        int slot = -1;
        for (int i2 = 0; i2 < 9; ++i2) {
            if (!mc.player.getInventory().getStack(i2).getItem().equals(item)) continue;
            slot = i2;
            break;
        }
        return slot;
    }


    public static ItemStack getStackInSlot(int i) {
        return mc.player.getInventory().getStack(i);
    }

    public static int findItem(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = getStackInSlot(i).getItem();
            if (Item.getRawId(item) != Item.getRawId(input)) continue;
            return i;
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        mc.player.getInventory().selectedSlot = slot;
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static int getBestTool(BlockState state) {
        int slot = getBestToolNoFallback(state);
        if (slot != -1) {
            return slot;
        }
        return mc.player.getInventory().selectedSlot;
    }

    public static int getBestToolNoFallback(BlockState state) {
        int slot = -1;
        float bestTool = 0.0f;
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ToolItem)) {
                continue;
            }
            float speed = stack.getMiningSpeedMultiplier(state);
            int efficiency = EnchantmentHelper.getLevel((RegistryEntry<Enchantment>) Enchantments.EFFICIENCY, stack);
            if (efficiency > 0) {
                speed += efficiency * efficiency + 1.0f;
            }
            if (speed > bestTool) {
                bestTool = speed;
                slot = i;
            }
        }
        return slot;
    }

    public int getItemSlot(Item item) {

        if (mc.player == null) return 0;

        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            if (i != 0 && i != 5 && i != 6 && i != 7) {
                if (i != 8) {
                    ItemStack s = mc.player.getInventory().getStack(i);
                    if (!s.isEmpty()) {
                        if (s.getItem() == item) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }
}
