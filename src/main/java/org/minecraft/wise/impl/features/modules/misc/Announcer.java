package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.chat.ChatMessage;
import org.minecraft.wise.api.utils.chat.ChatUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;

import java.util.Random;
import java.util.UUID;

public class Announcer extends Module {
    private final Value<String> mode = new ValueBuilder<String>().withDescriptor("Mode").withValue("English").withModes("English", "Swedish", "Russian", "Czech").register(this);
    private final Value<Boolean> client = new ValueBuilder<Boolean>().withDescriptor("Client").withValue(false).register(this);
    private final Value<Boolean> join = new ValueBuilder<Boolean>().withDescriptor("Join").withValue(true).register(this);
    private final Value<Boolean> leave  = new ValueBuilder<Boolean>().withDescriptor("Leave").withValue(true).register(this);
    private final Value<Boolean> eat = new ValueBuilder<Boolean>().withDescriptor("Eat").withValue(true).register(this);
    private final Value<Boolean> walk = new ValueBuilder<Boolean>().withDescriptor("Walk").withValue(true).register(this);
    private final Value<Boolean> mine = new ValueBuilder<Boolean>().withDescriptor("Mine").withValue(true).register(this);
    private final Value<Boolean> place = new ValueBuilder<Boolean>().withDescriptor("Place").withValue(true).register(this);
    private final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(5).withRange(0, 20).register(this);
    private final Timer timer = new Timer.Single();
    private final String[] leaveMessages = new String[7];
    private final String[] joinMessages = new String[7];
    private final Random random = new Random();


    public Announcer() {
        super("Announcer", Category.Misc);
        setLeaveMessages();
        setJoinMessages();
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (event.getPacket() instanceof PlayerListS2CPacket packet && join.getValue()) {
            if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                for (PlayerListS2CPacket.Entry list : packet.getPlayerAdditionEntries()) {
                    PlayerEntity player = mc.world.getPlayerByUuid(list.profileId());
                    if (timer.hasPassed(delay.getValue().intValue() * 1000L)) {
                        int value = random.nextInt(6);

                        if (!client.getValue()) {
                            mc.player.networkHandler.sendChatMessage(joinMessages[value].replace("%SERVERIP", mc.player.getServer().getServerIp())
                                    + player.getName().getString());
                        } else {
                            ChatUtils.sendMessage(new ChatMessage(joinMessages[value].replace("%SERVERIP", mc.player.getServer().getServerIp())
                                    + player.getName().getString(), false, 0));
                        }
                    }
                }
            }
        }

        if (event.getPacket() instanceof PlayerRemoveS2CPacket packet && leave.getValue()) {
            for (UUID uuid : packet.profileIds()) {
                PlayerEntity player = mc.world.getPlayerByUuid(uuid);
                if (timer.hasPassed(delay.getValue().intValue() * 1000L)) {
                    int value = random.nextInt(6);

                    if (!client.getValue()) {
                        mc.player.networkHandler.sendChatMessage(leaveMessages[value] + player.getName().getString());
                    } else {
                        ChatUtils.sendMessage(new ChatMessage(leaveMessages[value] + player.getName().getString(), false, 0));
                    }
                }
            }
        }
    }

    public void getLanguages() {

    }

    public void setLeaveMessages() {
        leaveMessages[0] = "See you later, ";
        leaveMessages[1] = "Catch ya later, ";
        leaveMessages[2] = "See you next time, ";
        leaveMessages[3] = "Farewell, ";
        leaveMessages[4] = "Bye, ";
        leaveMessages[5] = "Good bye, ";
        leaveMessages[6] = "Later, ";
    }

    public void setJoinMessages() {
        joinMessages[0] = "Good to see you, ";
        joinMessages[1] = "Greetings, ";
        joinMessages[2] = "Hello, ";
        joinMessages[3] = "Howdy, ";
        joinMessages[4] = "Hey, ";
        joinMessages[5] = "Good evening, ";
        joinMessages[6] = "Welcome to %SERVERIP%, ";
    }
}
