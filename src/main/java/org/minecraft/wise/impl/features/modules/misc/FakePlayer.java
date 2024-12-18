package org.minecraft.wise.impl.features.modules.misc;

import org.minecraft.wise.api.feature.module.Module;
import com.mojang.authlib.GameProfile;
import org.minecraft.wise.api.utils.NullUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FakePlayer extends Module {

    public FakePlayer() {
        super("FakePlayer", Category.Misc);
    }

    @Override
    public void onEnable() {
        if (NullUtils.nullCheck())
            return;

        Fake fake = new Fake(mc.world);

        fake.setId(-696420);
        fake.copyPositionAndRotation(mc.player);

        for (int i = 0; i < fake.getInventory().size(); i++) {
            fake.getInventory().setStack(i, mc.player.getInventory().getStack(i));
        }

        mc.world.addEntity(fake);
        fake.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
        fake.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0));
        fake.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3));
    }

    @Override
    public void onDisable() {
        if (NullUtils.nullCheck())
            return;

        mc.world.removeEntity(-696420, Entity.RemovalReason.DISCARDED);
    }

    public static class Fake extends AbstractClientPlayerEntity {
        public static final GameProfile RANDOM_GAMEPROFILE = new GameProfile(UUID.randomUUID(), "Subhuman");
        public static final PlayerListEntry DUMMY_PLAYERINFO = new PlayerListEntry(RANDOM_GAMEPROFILE, true);

        public Fake(ClientWorld clientWorld) {
            super(clientWorld, RANDOM_GAMEPROFILE);
        }

        public Fake(ClientWorld clientWorld, GameProfile gameProfile) {
            super(clientWorld, gameProfile);
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        protected PlayerListEntry getPlayerListEntry() {
            return DUMMY_PLAYERINFO;
        }

        @Override
        protected @NotNull MoveEffect getMoveEffect() {
            return MoveEffect.NONE;
        }

        @Override
        public boolean isSilent() {
            return true;
        }


        @Override
        protected void pushOutOfBlocks(double x, double y, double z) {
        }
    }


}
