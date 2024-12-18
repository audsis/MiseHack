package org.minecraft.wise.impl.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class NoSlow extends Module {

    public static NoSlow INSTANCE;
    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("Cancel").withModes("Cancel", "Grim").register(this);
    public final Value<Boolean> item = new ValueBuilder<Boolean>().withDescriptor("Items").withValue(true).register(this);
    private final Value<Boolean> guiMove = new ValueBuilder<Boolean>().withDescriptor("GuiMove").withValue(true).register(this);

    public NoSlow() {
        super("NoSlow", Category.Movement);
        INSTANCE = this;
        setDescription("Removes the slowdown when you eat.");
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (AutoWalk.INSTANCE.isEnabled())
            return;

        if (guiMove.getValue()) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
                for (KeyBinding k : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey})
                    k.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));

                if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 264))
                    mc.player.setPitch(mc.player.getPitch() + 5.0f);

                if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 265))
                    mc.player.setPitch(mc.player.getPitch() - 5.0f);

                if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 262))
                    mc.player.setYaw(mc.player.getYaw() + 5.0f);

                if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 263))
                    mc.player.setYaw(mc.player.getYaw() - 5.0f);

                if (mc.player.getPitch() > 90.0f)
                    mc.player.setYaw(90.0f);

                if (mc.player.getPitch() < -90.0f)
                    mc.player.setYaw(-90.0f);
            }
        }
        
        if (mode.getValue().equals("Grim") && mc.player.isUsingItem() && !mc.player.isRiding() && !mc.player.isFallFlying()) {
            if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
                send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            }
        }
    }

    public boolean canNoSlow() {
        return !isEnabled() || !mode.getValue().equals("Grim") || mc.player.getActiveHand() != Hand.MAIN_HAND;
    }
}
