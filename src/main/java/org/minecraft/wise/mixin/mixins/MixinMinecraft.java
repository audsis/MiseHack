package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.event.TickEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.management.TimerManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.misc.MultiTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {


    @Inject(method = "<init>", at = @At("TAIL"))
    void postWindowInit(RunArgs args, CallbackInfo ci) {
        try {
            FontManager.gang = FontManager.createDefault();
            FontManager.neverloseSmall = FontManager.create("small", 18);
            FontManager.neverloseBig = FontManager.create("big", 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tick(CallbackInfo callbackInfo) {
        if (NullUtils.nullCheck())
            return;

        TickEvent event = new TickEvent();
        Bus.EVENT_BUS.post(event);

        TimerManager.INSTANCE.update();
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void postInit(RunArgs args, CallbackInfo ci) {
        WiseMod.postInit();
    }

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean isUsingItem(ClientPlayerEntity instance) {
        if (MultiTask.INSTANCE.isEnabled()) {
            return false;
        } else {
            return instance.isUsingItem();
        }
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet" + "/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    private boolean isBreakingBlock(ClientPlayerInteractionManager instance) {
        if (MultiTask.INSTANCE.isEnabled()) {
            return false;
        } else {
            return instance.isBreakingBlock();
        }
    }
}

