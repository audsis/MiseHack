package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.MouseEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.management.InventoryManager;
import org.minecraft.wise.api.management.RotationManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.utils.player.InventoryUtils;
import org.minecraft.wise.api.utils.rotation.RotationPoint;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class MiddleClick extends Module {
    private final Value<Boolean> friend = new ValueBuilder<Boolean>().withDescriptor("Friend").withValue(true).register(this);
    private final Value<Boolean> pearl = new ValueBuilder<Boolean>().withDescriptor("Pearl").withValue(true).register(this);
    private final Value<Boolean> xp = new ValueBuilder<Boolean>().withDescriptor("XP").withValue(true).register(this);
    private final Value<Boolean> xpRotate = new ValueBuilder<Boolean>().withDescriptor("XPRotate").withValue(true).register(this);
    private final Value<Boolean> firework = new ValueBuilder<Boolean>().withDescriptor("Firework").withValue(true).register(this);
    private boolean hasPressed = false;

    public MiddleClick() {
        super("MiddleClick", Category.Misc);
    }

    @Subscribe
    public void onMouse(MouseEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && event.getAction() == GLFW.GLFW_PRESS && mc.currentScreen == null) {
            int oldSlot;
            Entity pointed = mc.targetedEntity;

            if (!hasPressed) {
                if (friend.getValue() && pointed != null) {
                    if (FriendManager.INSTANCE.isFriend(pointed)) {
                        FriendManager.INSTANCE.removeFriend(pointed);
                        ChatUtils.sendMessage(new ChatMessage("Removed " + pointed.getName() + " from friends", false, 0));
                    } else {
                        FriendManager.INSTANCE.addFriend(pointed);
                        ChatUtils.sendMessage(new ChatMessage("Added " + pointed.getName() + " from friends", false, 0));
                    }
                }

                if (pointed == null && pearl.getValue() && allow()) {
                    oldSlot = mc.player.getInventory().selectedSlot;
                    int pearlSlot = InventoryUtils.getHotbarItemSlot(Items.ENDER_PEARL);

                    if (pearlSlot == -1) {
                        ChatUtils.sendMessage(new ChatMessage("No pearls in hotbar", false, 0));
                        hasPressed = true;
                        return;
                    }
                    InventoryManager.INSTANCE.setSlot(pearlSlot);
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    InventoryManager.INSTANCE.setSlot(oldSlot);
                }
            }

            if (xp.getValue() && allowExp()) {
                oldSlot = mc.player.getInventory().selectedSlot;
                int xpSlot = InventoryUtils.getHotbarItemSlot(Items.EXPERIENCE_BOTTLE);

                if (xpSlot == -1) {
                    hasPressed = true;
                    return;
                }

                InventoryManager.INSTANCE.setSlot(xpSlot);
                if (xpRotate.getValue()) {
                    RotationManager.INSTANCE.setRotationPoint(new RotationPoint(mc.player.getYaw(), 0, 12, false));
                }
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                InventoryManager.INSTANCE.setSlot(oldSlot);
            }

            if (firework.getValue() && allow()) {
                oldSlot = mc.player.getInventory().selectedSlot;
                int firework = InventoryUtils.getHotbarItemSlot(Items.FIREWORK_ROCKET);

                if (firework == -1) {
                    hasPressed = true;
                    return;
                }

                InventoryManager.INSTANCE.setSlot(firework);
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                InventoryManager.INSTANCE.setSlot(oldSlot);
            }


            hasPressed = true;
        } else {
            hasPressed = false;
        }
    }

    private boolean allow() {
        HitResult mouseOver = mc.crosshairTarget;
        return mouseOver == null || mouseOver.getType() == HitResult.Type.MISS;
    }

    private boolean allowExp() {
        HitResult mouseOver = mc.crosshairTarget;
        return mouseOver != null && mouseOver.getType() == HitResult.Type.BLOCK;
    }
}
