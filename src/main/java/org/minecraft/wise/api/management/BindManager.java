package org.minecraft.wise.api.management;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.InputEvent;
import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.binds.IBindable;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BindManager implements IMinecraft {
    public static BindManager INSTANCE;
    List<IBindable> bindables = new ArrayList<>();

    public BindManager() {
        Bus.EVENT_BUS.register(this);
    }

    public List<IBindable> getBindables() {
        return this.bindables;
    }

    public void setBindables(List<IBindable> bindables) {
        this.bindables = bindables;
    }

    @Subscribe
    public void onKey(InputEvent event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        if (mc.currentScreen instanceof ChatScreen) return;

        for (IBindable bindable : getBindables()) {
            if (bindable.getKey() != event.getKey()) continue;
            bindable.onKey();
        }
    }
}