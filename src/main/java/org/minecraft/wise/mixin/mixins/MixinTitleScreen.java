package org.minecraft.wise.mixin.mixins;

import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen implements IMinecraft {

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        //mc.setScreen(new WiseMainMenu());
    }
}
