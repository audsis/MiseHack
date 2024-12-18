package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.render.NametagsUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import org.minecraft.wise.api.wrapper.IMinecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutESP extends Module {
    public Value<Boolean> nametags = new ValueBuilder<Boolean>().withDescriptor("Nametags").withValue(true).register(this);
    public Value<Boolean> distance = new ValueBuilder<Boolean>().withDescriptor("Distance").withValue(true).register(this);
    public Value<Boolean> pops = new ValueBuilder<Boolean>().withDescriptor("Pops").withValue(true).register(this);
    public Value<Boolean> health = new ValueBuilder<Boolean>().withDescriptor("Health").withValue(true).register(this);
    public Value<Color> borderColor = new ValueBuilder<Color>().withDescriptor("Border Color").withValue(new Color(224, 34, 34, 255)).register(this);
    public Value<Color> boxColor = new ValueBuilder<Color>().withDescriptor("Background Color").withValue(new Color(25, 25, 25, 130)).register(this);
    public Value<Boolean> box = new ValueBuilder<Boolean>().withDescriptor("Box").withValue(true).register(this);
    public Value<Color> boxerColor = new ValueBuilder<Color>().withDescriptor("Box Color").withValue(new Color(224, 34, 34, 255)).register(this);
    private final Map<UUID, LogSpots> spots = new ConcurrentHashMap<>();

    public LogoutESP() {
        super("LogoutESP", Category.Render);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (event.getPacket() instanceof PlayerListS2CPacket packet) {
            if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                for (PlayerListS2CPacket.Entry list : packet.getPlayerAdditionEntries()) {
                    PlayerEntity player = mc.world.getPlayerByUuid(list.profileId());

                    if (player == mc.player || player == null)
                        return;

                    LogSpots spot = new LogSpots(player);
                    spots.remove(player.getUuid(), spot);
                }
            }
        }
        if (event.getPacket() instanceof PlayerRemoveS2CPacket(List<UUID> profileIds)) {
            for (UUID uuid : profileIds) {
                PlayerEntity player = mc.world.getPlayerByUuid(uuid);

                if (player == mc.player || player == null)
                    return;

                LogSpots spot = new LogSpots(player);
                spots.put(player.getUuid(), spot);
            }
        }
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck() || spots.isEmpty())
            return;

        ArrayList<LogSpots> spot = new ArrayList<>(spots.values());
        spot.sort(Comparator.comparing(LogSpots::getDistance));

        for (LogSpots spots : spot) {
            PlayerEntity player = spots.getEntity();
            Box bb = RenderUtils.interpolate(spots.getBoundingBox());

            if (box.getValue()) {
                event.getMatrices().push();
                RenderUtils.drawOutlineBox(event.getMatrices(), spots.getBoundingBox(), boxerColor.getValue(), 1.0f);
                event.getMatrices().pop();
            }

            if (nametags.getValue()) {
                String[] text = new String[]{ getEntityName(player, spots.getName(), spots.getDistance()) };

                drawTag(event, text, getEntityColor(player), bb);
                event.getMatrices().pop();
            }
        }
    }

    private void drawTag(Render3dEvent event, String[] text, Color color, Box interpolated) {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;

        NametagsUtil.drawNametag(x, y + 1.4f, z, text, color, boxColor.getValue(), borderColor.getValue(), event.getMatrices());
    }

    private Color getEntityColor(PlayerEntity entity) {
        if (FriendManager.INSTANCE.isFriend(entity)) {
            return new Color(69, 242, 255, 255);
        }

        return new Color(255, 0, 0);
    }

    private String getEntityName(PlayerEntity entity, String name, double distance2) {
        String string = name + " logout ";

        String color;
        double ceil = Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());

        if (ceil > 18) {
            color = Formatting.GREEN.toString();
        } else if (ceil > 16) {
            color = Formatting.DARK_GREEN.toString();
        } else if (ceil > 12) {
            color = Formatting.YELLOW.toString();
        } else if (ceil > 8) {
            color = Formatting.GOLD.toString();
        } else if (ceil > 5) {
            color = Formatting.RED.toString();
        } else {
            color = Formatting.DARK_RED.toString();
        }

        if (health.getValue()) {
            string += color + ceil;
        }

        if (distance.getValue() || pops.getValue()) {
            String whatever = "";

            if (distance.getValue()) {
                whatever += Math.ceil(distance2) + "m";
            }

//            if (pops.getValue()) {
//            }

            String bracketed = Formatting.DARK_GRAY + " (" + Formatting.WHITE + whatever + Formatting.DARK_GRAY + ")";

            string = string + bracketed;
        }

        return string;
    }

    public static class LogSpots implements IMinecraft {
        private final String name;
        private final Box boundingBox;
        private final PlayerEntity entity;
        private final double x;
        private final double y;
        private final double z;

        public LogSpots(PlayerEntity player) {
            this.name = player.getName().getString();
            this.boundingBox = player.getBoundingBox();
            this.entity = player;
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        }

        public String getName() {
            return name;
        }

        public PlayerEntity getEntity() {
            return entity;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public double getDistance() {
            Vec3d interpolation = RenderUtils.interpolateEntity(mc.player);

            double xDist = interpolation.x - x;
            double yDist = interpolation.y - y;
            double zDist = interpolation.z - z;
            return MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
        }

        public Box getBoundingBox() {
            return boundingBox;
        }
    }
}
