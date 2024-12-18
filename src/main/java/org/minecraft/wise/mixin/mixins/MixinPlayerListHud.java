package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.features.modules.misc.ExtraTab;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud implements IMinecraft {

    @Shadow
    @Final
    private static Comparator<PlayerListEntry> ENTRY_ORDERING;

    @Inject(method = "getPlayerName", at = @At(value = "HEAD"), cancellable = true)
    private void setPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (mc.player != null) {
            String text = entry.getProfile().getName();

            if (ExtraTab.INSTANCE.isEnabled()) {
                if (FriendManager.INSTANCE.isFriend(text)) {
                    cir.cancel();
                    MutableText texter = Text.literal(text);

                    cir.setReturnValue(texter.setStyle(texter.getStyle().withColor(ExtraTab.INSTANCE.friendColor.getValue().getRGB())));
                }

                if (text.equals(mc.player.getName().getString())) {
                    cir.cancel();
                    MutableText texter = Text.literal(mc.player.getName().getString());

                    cir.setReturnValue(texter.setStyle(texter.getStyle().withColor(ExtraTab.INSTANCE.selfColor.getValue().getRGB())));
                }
            }
        }
    }

    @Inject(method = "collectPlayerEntries", at = @At(value = "HEAD"), cancellable = true)
    private void hookCollectPlayerEntries(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (ExtraTab.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue(mc.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(ExtraTab.INSTANCE.size.getValue().longValue()).toList());
        }
    }
}
