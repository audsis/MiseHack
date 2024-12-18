package org.minecraft.wise.api.feature.module;

import org.minecraft.wise.api.binds.IBindable;
import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.management.BindManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.value.custom.Bind;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.WiseMod;
import org.minecraft.wise.impl.features.modules.client.Manager;
import org.minecraft.wise.mixin.mixins.access.IClientWorld;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Formatting;

import java.util.Map;

public class Module extends Feature implements IBindable, IMinecraft {
    final Value<Boolean> chatNotify = new ValueBuilder<Boolean>().withDescriptor("Chat Notify").withValue(true).register(this);
    Bind bind;

    public Module(String name, Feature.Category category) {
        super(name, category, Feature.FeatureType.Module);
        bind = new Bind();
        BindManager.INSTANCE.getBindables().add(this);
    }

    public Value<?> register(Value<?> value) {
        getValues().add(value);
        return value;
    }

    public Bind getBind() {
        return this.bind;
    }

    public void setBind(Bind bind) {
        this.bind = bind;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (Manager.INSTANCE.moduleNotifications.getValue()) {
            WiseMod.notificationProcessor.addNotification(Formatting.RESET + getDisplayName() + Formatting.WHITE + " was " + "enabled" + ".", 500L);
        }

        if (chatNotify.getValue()) {
            String bold = Formatting.BOLD + "";
            if (!Manager.INSTANCE.bold.getValue()) {
                bold = "";
            }
            ChatUtils.sendMessage(new ChatMessage(Formatting.WHITE + bold + getDisplayName() + Formatting.RESET + Formatting.WHITE + " was "  + "enabled" + ".", false, 0));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (Manager.INSTANCE.moduleNotifications.getValue()) {
            WiseMod.notificationProcessor.addNotification(Formatting.RESET + getDisplayName() + Formatting.WHITE + " was " + Formatting.GRAY + "disabled" + Formatting.WHITE + ".", 500L);
        }

        if (chatNotify.getValue()) {
            String bold = Formatting.BOLD + "";
            if (!Manager.INSTANCE.bold.getValue()) {
                bold = "";
            }
            ChatUtils.sendMessage(new ChatMessage(Formatting.WHITE + bold + getDisplayName() + Formatting.RESET + Formatting.WHITE + " was " + Formatting.GRAY + "disabled" + Formatting.WHITE + ".", false, 0));
        }
    }


    public void send(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;

        mc.getNetworkHandler().sendPacket(packet);
    }

    public void sendSeq(SequencedPacketCreator packetCreator) {
        if (mc.getNetworkHandler() == null || NullUtils.nullCheck())
            return;

        PendingUpdateManager sequence = ((IClientWorld) mc.world).accessPendingUpdateManager().incrementSequence();
        Packet<?> packet = packetCreator.predict(sequence.getSequence());

        mc.getNetworkHandler().sendPacket(packet);

        sequence.close();
    }

    @Override
    public void load(Map<String, Object> objects) {
        super.load(objects);
        bind.setKey((Integer) objects.get("bind"));
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> toSave = super.save();
        toSave.put("bind", this.bind.getKey());
        return toSave;
    }

    @Override
    public int getKey() {
        return bind.getKey();
    }

    @Override
    public void onKey() {
        toggle();
    }
}