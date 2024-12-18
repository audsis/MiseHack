package org.minecraft.wise.impl.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ConcurrentSet;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.Set;

public class UnicodeSpammer extends Module {

    public final static String LAG_MESSAGE =
                    "āȁ́Ё؁܁ࠁँਁ" +
                    "ଁก༁ခᄁሁጁᐁᔁ" +
                    "ᘁᜁ᠁ᤁᨁᬁᰁᴁḁ" +
                    "ἁ℁∁⌁␁━✁⠁⤁" +
                    "⨁⬁Ⰱⴁ⸁⼁、㄁㈁" +
                    "㌁㐁㔁㘁㜁㠁㤁㨁㬁" +
                    "㰁㴁㸁㼁䀁䄁䈁䌁䐁" +
                    "䔁䘁䜁䠁䤁䨁䬁䰁䴁" +
                    "丁企倁儁刁匁吁唁嘁" +
                    "圁堁夁威嬁封崁币弁" +
                    "态愁戁持搁攁昁朁栁" +
                    "椁樁欁氁洁渁漁瀁焁" +
                    "爁猁琁甁瘁省码礁稁" +
                    "笁簁紁縁缁老脁舁茁" +
                    "萁蔁蘁蜁蠁褁訁謁谁" +
                    "贁踁輁送鄁鈁錁鐁锁" +
                    "阁霁頁餁騁鬁鰁鴁鸁" +
                    "鼁ꀁꄁꈁꌁꐁꔁꘁ꜁" +
                    "ꠁ꤁ꨁꬁ각괁긁꼁뀁" +
                    "넁눁댁됁딁똁뜁렁뤁" +
                    "먁묁밁봁";

    private final Value<Number> delay = new ValueBuilder<Number>().withDescriptor("Delay").withValue(5).withRange(0, 20).register(this);
    private final Value<Boolean> smart = new ValueBuilder<Boolean>().withDescriptor("Smart").withValue(false).register(this);
    private final Timer timer = new Timer.Single();
    private final Set<String> sent = new ConcurrentSet<>();

    public UnicodeSpammer() {
        super("UnicodeSpammer", Category.Misc);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) return;

        if (timer.hasPassed(delay.getValue().intValue() * 1000L)) {
            if (event.getPacket() instanceof EntityStatusS2CPacket packet && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                if (packet.getEntity(mc.world) instanceof PlayerEntity player && player != mc.player && !FriendManager.INSTANCE.isFriend(packet.getEntity(mc.world))) {

                    if (smart.getValue() && !sent.contains(player.getName().getString())) return;

                    mc.player.networkHandler.sendChatMessage("/msg " + player.getName().getString() + " " + LAG_MESSAGE);
                    sent.add(player.getName().getString());
                }
            }
        }
    }
}
