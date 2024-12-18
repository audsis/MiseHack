package org.minecraft.wise.api.management;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.client.Manager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import java.util.ArrayList;

public class InventoryManager implements IMinecraft {
    public static InventoryManager INSTANCE;

    private int slot;

    public InventoryManager() {
        Bus.EVENT_BUS.register(this);
    }

    @Subscribe
    public void onPacketOutBound(PacketEvent event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            slot = packet.getSelectedSlot();
        }

        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket packet) {
            slot = packet.getSlot();
        }
    }

    public void setSlot(final int barSlot) {
        if (slot != barSlot && PlayerInventory.isValidHotbarIndex(barSlot)) {
            if (Manager.INSTANCE.silent.getValue().equals("Normal")) {
                setSlotForced(barSlot);
            } else {
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                        barSlot + 36,
                        mc.player.getInventory().selectedSlot,
                        SlotActionType.SWAP,
                        mc.player);
            }
        }
    }

    public void setClientSlot(final int barSlot) {
        if (mc.player.getInventory().selectedSlot != barSlot && PlayerInventory.isValidHotbarIndex(barSlot)) {
            mc.player.getInventory().selectedSlot = barSlot;
            setSlotForced(barSlot);
        }
    }

    public void setSlotForced(final int barSlot) {
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(barSlot));
    }

    public void syncToClient() {
        if (isDesynced()) {
            setSlotForced(mc.player.getInventory().selectedSlot);
        }
    }

    public boolean isDesynced() {
        return mc.player.getInventory().selectedSlot != slot;
    }


    public void closeScreen() {
        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
    }

    public void pickupSlot(final int slot) {
        click(slot, 0, SlotActionType.PICKUP);
    }

    public void quickMove(final int slot) {
        click(slot, 0, SlotActionType.QUICK_MOVE);
    }

    public void throwSlot(final int slot) {
        click(slot, 0, SlotActionType.THROW);
    }

    public void swapSlot(final int slot) {
        click(slot, 0, SlotActionType.SWAP);
    }


    private void click(int slot, int button, SlotActionType type) {
        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);

        for (Slot slot1 : defaultedList) {
            list.add(slot1.getStack().copy());
        }

        screenHandler.onSlotClick(slot, button, type, mc.player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();

        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }

        mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }

    public int count(Item item) {
        ItemStack offhandStack = mc.player.getOffHandStack();
        int itemCount = offhandStack.getItem() == item ? offhandStack.getCount() : 0;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = mc.player.getInventory().getStack(i);
            if (slot.getItem() == item) {
                itemCount += slot.getCount();
            }
        }
        return itemCount;
    }

    public int getServerSlot() {
        return slot;
    }

    public ItemStack getServerItem() {
        if (mc.player != null && getServerSlot() != -1) {
            return mc.player.getInventory().getStack(getServerSlot());
        }
        return null;
    }
}