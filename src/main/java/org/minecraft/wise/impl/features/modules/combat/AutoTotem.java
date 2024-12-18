package org.minecraft.wise.impl.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.player.InventoryUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;

public class AutoTotem extends Module {

    private final Value<String> item = new ValueBuilder<String>().withDescriptor("Item").withValue("Totem").withModes("Totem", "Gapple", "Crystal", "Bow").register(this);
    private final Value<Number> health = new ValueBuilder<Number>().withDescriptor("Health").withValue(16).withRange(0, 36).register(this);
    public final Value<Boolean> swordGap = new ValueBuilder<Boolean>().withDescriptor("SwordGap").withValue(true).register(this);
    private final Value<Number> fallDistance = new ValueBuilder<Number>().withDescriptor("FallDistance").withValue(16).withRange(0, 36).register(this);
    public final Value<Boolean> totemOnElytra = new ValueBuilder<Boolean>().withDescriptor("TotemOnElytra").withValue(true).register(this);
    private final Value<String> method = new ValueBuilder<String>().withDescriptor("Method").withValue("New").withModes("New", "Old").register(this);

    public AutoTotem() {
        super("AutoTotem", Category.Combat);
        setDescription("Automatically puts a totem or crystal in your offhand.");
    }

    @Subscribe
    public void onUpdate(TickEvent event) {

        if (NullUtils.nullCheck())
            return;

        ScreenHandler screenHandler = mc.player.currentScreenHandler;

        Item item = getWeldingItemType();

        if (mc.player.getOffHandStack().getItem() == item)
            return;

        if (mc.currentScreen != null)
            return;

        int slot = getItemSlot(item);

        if (slot == -1)
            return;

        if (method.getValue().equals("New")) {
            InventoryUtils.fastOffhand(slot);
        } else {
            InventoryUtils.offhand(slot);
        }
    }

    public Item getWeldingItemType() {

        boolean elytra = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.isFallFlying() && totemOnElytra.getValue();

        float healthin = health.getValue().floatValue();

        if (mc.player.fallDistance >= fallDistance.getValue().floatValue() || (mc.player.getAbsorptionAmount() + mc.player.getHealth()) <= healthin || elytra)
            return Items.TOTEM_OF_UNDYING;

        if (mc.player.getMainHandStack().getItem() == getSword() && mc.options.useKey.isPressed() && swordGap.getValue())
            return getGoldenApple();

        return getItemFromMode();
    }

    public Item getItemFromMode() {
        return switch (item.getValue()) {
            case "Crystal" -> Items.END_CRYSTAL;
            case "Gapple" -> getGoldenApple();
            case "Sword" -> getSword();
            case "Bow" -> Items.BOW;
            default -> Items.TOTEM_OF_UNDYING;
        };

    }

    public Item getGoldenApple() {
        if (getItemCount(Items.ENCHANTED_GOLDEN_APPLE) == -1) return Items.GOLDEN_APPLE;

        return Items.ENCHANTED_GOLDEN_APPLE;
    }

    public Item getSword() {
        if (getItemCount(Items.NETHERITE_SWORD) == -1) return Items.DIAMOND_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1)
            return Items.IRON_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1 && getItemCount(Items.IRON_SWORD) == -1)
            return Items.STONE_SWORD;

        if (getItemCount(Items.NETHERITE_SWORD) == -1 && getItemCount(Items.DIAMOND_SWORD) == -1 && getItemCount(Items.IRON_SWORD) == -1 && getItemCount(Items.STONE_SWORD) == -1)
            return Items.WOODEN_SWORD;

        return Items.NETHERITE_SWORD;
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

    @Override
    public String getHudInfo() {
        return getItemCount(Items.TOTEM_OF_UNDYING) + "";
    }
}