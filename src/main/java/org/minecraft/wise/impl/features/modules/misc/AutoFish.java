package org.minecraft.wise.impl.features.modules.misc;

import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.math.MathUtil;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.item.FishingRodItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.feature.module.Module;

public class AutoFish extends Module {

    private final Timer cooldown = new Timer.Single();
    private final Timer timeout = new Timer.Single();

    public AutoFish() {
        super("AutoFish", Category.Misc);
        setDescription("Automatically fishes for you lazy prick");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        cooldown.reset();
        timeout.reset();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (NullUtils.nullCheck())
            return;

        useItemInHand();
    }

    private void catchFish() {
        useItemInHand();
        try {
            Thread.sleep(MathUtil.getRandomNumber(500, 500));
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        useItemInHand();
        timeout.reset();
    }

    @Subscribe
    public void onPacketReceive(PacketEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket sound) {
            if (sound.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) && mc.player.fishHook != null && mc.player.fishHook.squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ()) < 4.0) {
                catchFish();
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!cooldown.hasPassed(1000L))
            return;

        if (timeout.hasPassed(45000L) && mc.player.getMainHandStack().getItem() instanceof FishingRodItem) {
            useItemInHand();
            timeout.reset();
            cooldown.reset();
        }
    }

    private void useItemInHand() {
        if (mc.player.getMainHandStack().getItem() instanceof FishingRodItem) {
            send(new HandSwingC2SPacket(Hand.MAIN_HAND));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
