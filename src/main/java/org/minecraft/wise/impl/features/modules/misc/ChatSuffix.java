package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.MessageEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.impl.WiseMod;

public class ChatSuffix extends Module {

    private final String[] filters = new String[]{".", "/", ",", ":", "`", "-"};
    public ChatSuffix() {
        super("ChatSuffix", Category.Misc);
    }

    @Subscribe
    private void onMessage(MessageEvent event) {
        String message = event.message;

        if (allowMessage(message)) {
            message = message + " " + WiseMod.NAME_UNICODE;

            event.message = message;
        }
    }

    private boolean allowMessage(String message) {
        boolean allow = true;
        for (String s2 : filters) {
            if (!message.startsWith(s2)) continue;
            allow = false;
            break;
        }
        return allow;
    }

}
