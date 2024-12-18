package org.minecraft.wise.api.utils.world;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import java.util.List;

public class EnchantmentRenderer
{
    public static double goffset;

    public static void updateGlobalOffset(List<ItemStack> items, double offY) {
        goffset = offY;
        for (ItemStack item : items) {
            if (EnchantmentHelper.getEnchantments(item).getSize() == 6) {
                goffset += 0.7;
                break;
            }
            if (EnchantmentHelper.getEnchantments(item).getSize() == 7) {
                ++goffset;
                break;
            }
            if (EnchantmentHelper.getEnchantments(item).getSize() == 5) {
                goffset += 0.4;
                break;
            }
        }
    }
}
