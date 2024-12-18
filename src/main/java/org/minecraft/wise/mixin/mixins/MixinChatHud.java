package org.minecraft.wise.mixin.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.OrderedText;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.utils.render.TextUtils;
import org.minecraft.wise.impl.features.modules.client.FontMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;" + "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;" + "Lnet/minecraft/text/OrderedText;III)I"))
    private int render(DrawContext instance, TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
        FontManager.drawText(instance, TextUtils.parseOrderedText(text).getString(), x, y, color);
        return 0;
    }
}
