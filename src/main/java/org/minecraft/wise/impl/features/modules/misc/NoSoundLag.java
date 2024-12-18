package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Set;

public class NoSoundLag extends Module {

    protected static final Set<RegistryEntry<SoundEvent>> LAG_SOUNDS = Sets.newHashSet
            (
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                    SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA,
                    SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    SoundEvents.ITEM_ARMOR_EQUIP_GOLD,
                    SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER
            );


    public NoSoundLag() {
        super("NoSoundLag", Category.Misc);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (!(LAG_SOUNDS.contains(packet.getSound()))) return;

            event.cancel();
        }
    }
}
