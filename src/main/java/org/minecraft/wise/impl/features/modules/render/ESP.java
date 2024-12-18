package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import org.minecraft.wise.api.event.PacketEvent;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.Timer;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.render.WorldRenderer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Objects;

public class ESP extends Module {
    public Value<Boolean> chorus = new ValueBuilder<Boolean>().withDescriptor("Chorus").withValue(true).register(this);
    public Value<Boolean> items = new ValueBuilder<Boolean>().withDescriptor("Items").withValue(true).register(this);
    public Value<Boolean> pearl = new ValueBuilder<Boolean>().withDescriptor("Pearl").withValue(true).register(this);
    public Value<Boolean> players = new ValueBuilder<Boolean>().withDescriptor("Players").withValue(true).register(this);
    public final Value<Color> color = new ValueBuilder<Color>().withDescriptor("Color").withValue(new Color(0, 150, 255)).register(this);
    private BlockPos chorusPos;
    private final Timer timer = new Timer.Single();

    public ESP() {
        super("ESP", Category.Render);
        setDescription("Lets you see entities through walls.");
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        if (chorus.getValue() && chorusPos != null) {

            if (timer.hasPassed(2500)) {
                chorusPos = null;
                return;
            }

            WorldRenderer.drawText("*chorus*", chorusPos.getX(), chorusPos.getY(), chorusPos.getZ(), 1.0f, -1);
        }

        for (Entity entity : mc.world.getEntities()) {
            if (players.getValue() && entity instanceof PlayerEntity playa) {
                RenderUtils.drawBox(event.getMatrices(), playa.getBoundingBox(), ColorUtil.newAlpha(color.getValue(), 74));
                RenderUtils.drawOutlineBox(event.getMatrices(), playa.getBoundingBox(), color.getValue(), 1.0f);
            }

            if (entity instanceof ItemEntity item && items.getValue()) {
                ItemStack stack = item.getStack();

                WorldRenderer.drawText(stack.getCount() > 1 ? stack.getName().getString() + " x" + stack.getCount() : stack.getName().getString(),
                        item.getX(),
                        item.getY(),
                        item.getZ(),
                        1.0f, -1);
            }

            if (entity instanceof EnderPearlEntity pearls && pearl.getValue()) {

                WorldRenderer.drawText(Objects.requireNonNull(pearls.getOwner()).getName().getString(),
                        pearls.getX(),
                        pearls.getY(),
                        pearls.getZ(),
                        1.0f,
                        -1);
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket sound) {
            if (sound.getSound().value() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT || sound.getSound().value()  == SoundEvents.ENTITY_ENDERMAN_TELEPORT) {
                chorusPos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
                timer.reset();
            }
        }
    }
}
