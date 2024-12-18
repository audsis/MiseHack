package org.minecraft.wise.impl.features.modules.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import org.minecraft.wise.api.event.Render3dEvent;
import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.management.FontManager;
import org.minecraft.wise.api.management.FriendManager;
import org.minecraft.wise.api.utils.NullUtils;
import org.minecraft.wise.api.utils.color.ColorUtil;
import org.minecraft.wise.api.utils.render.NametagsUtil;
import org.minecraft.wise.api.utils.render.RenderUtils;
import org.minecraft.wise.api.utils.render.TextUtils;
import org.minecraft.wise.api.utils.render.WorldRenderer;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class Nametags extends Module {

    public static Nametags INSTANCE;
    public Value<Color> borderColor = new ValueBuilder<Color>().withDescriptor("Border Color").withValue(new Color(234, 140, 250, 255)).register(this);
    public Value<Color> boxColor = new ValueBuilder<Color>().withDescriptor("Background Color").withValue(new Color(25, 25, 25, 130)).register(this);
    public Value<Color> normalColor = new ValueBuilder<Color>().withDescriptor("Normal Color").withValue(new Color(255, 255, 255, 255)).register(this);
    public Value<Color> FriendsColor = new ValueBuilder<Color>().withDescriptor("Friends Color").withValue(new Color(0, 255, 255, 255)).register(this);
    public Value<Color> SneakingColor = new ValueBuilder<Color>().withDescriptor("Sneaking Color").withValue(new Color(255, 153, 0)).register(this);
    public Value<Boolean> ping = new ValueBuilder<Boolean>().withDescriptor("Ping").withValue(true).register(this);
    public Value<Boolean> durability = new ValueBuilder<Boolean>().withDescriptor("Durability").withValue(true).register(this);
    public Value<Boolean> pops = new ValueBuilder<Boolean>().withDescriptor("Pops").withValue(true).register(this);
    public Value<Boolean> itemName = new ValueBuilder<Boolean>().withDescriptor("ItemName").withValue(true).register(this);
    public Value<Boolean> enchantNames = new ValueBuilder<Boolean>().withDescriptor("EnchantName").withValue(true).register(this);
    public Value<Boolean> health = new ValueBuilder<Boolean>().withDescriptor("Health").withValue(true).register(this);
    public Value<Boolean> healthCalc = new ValueBuilder<Boolean>().withDescriptor("HealthCalc").withValue(false).register(this);
    public Value<Boolean> armor = new ValueBuilder<Boolean>().withDescriptor("Armor").withValue(true).register(this);
    public Value<Boolean> entityId = new ValueBuilder<Boolean>().withDescriptor("EntityId").withValue(true).register(this);
    public Value<Boolean> gamemode = new ValueBuilder<Boolean>().withDescriptor("Gamemode").withValue(true).register(this);
    private final Value<Boolean> ColoredPing = new ValueBuilder<Boolean>().withDescriptor("ColoredPing").withValue(true).register(this);


    public Nametags() {
        super("Nametags", Category.Render);
        INSTANCE = this;
        setDescription("Displays information about the player.");
    }

    @Subscribe
    public void onRender3d(Render3dEvent event) {
        if (NullUtils.nullCheck())
            return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player) {

                if (player == mc.player || player.isDead())
                    continue;

                Vec3d uwu = RenderUtils.interpolateEntity(player);

                renderNametags(player, uwu.x, uwu.y, uwu.z, event.getMatrices());
            }
        }
    }

    private void renderNametags(PlayerEntity player, double n2, double distance, double n22, MatrixStack matrices) {
        double tempY = distance;
        tempY += player.isSneaking() ? 0.5 : 0.7;

        String[] text = new String[]{ renderEntityName(player) };

        NametagsUtil.drawNametag(n2, tempY + 1.4f, n22, text, getNameColor(player), boxColor.getValue(), borderColor.getValue(), matrices);

        ItemStack heldItemMainhand = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = player.getStackInHand(Hand.OFF_HAND);

        int xOffset = 0;
        int enchantOffset = 0;
        int armorSize;

        for (int i = armorSize = 3; i >= 0; i = --armorSize) {
            ItemStack itemStack;

            if (!(itemStack = player.getInventory().getArmorStack(armorSize)).isEmpty()) {
                xOffset -= 8;
                int size;
                if (enchantNames.getValue() && (size = EnchantmentHelper.getEnchantments(itemStack).getSize()) > enchantOffset)
                    enchantOffset = size;
            }
        }

        if ((!offhand.isEmpty() && armor.getValue()) || (durability.getValue() && offhand.isDamageable())) {
            xOffset -= 8;
            int size2;
            if (enchantNames.getValue() && (size2 = EnchantmentHelper.getEnchantments(offhand).getSize()) > enchantOffset)
                enchantOffset = size2;

        }

        if (!heldItemMainhand.isEmpty()) {

            int size3;
            if (enchantNames.getValue() && (size3 = EnchantmentHelper.getEnchantments(heldItemMainhand).getSize()) > enchantOffset)
                enchantOffset = size3;

            int armorOffset = getOffset(enchantOffset);

            if (armor.getValue() || (durability.getValue() && heldItemMainhand.isDamageable()))
                xOffset -= 8;

            if (armor.getValue()) {
                int oldOffset = armorOffset;
                armorOffset -= 32;
                renderStack(heldItemMainhand, xOffset, oldOffset, enchantOffset, matrices, player);
            }

            if (durability.getValue() && heldItemMainhand.isDamageable())
                renderDurability(heldItemMainhand, (float) xOffset, (float) armorOffset, matrices);

            if (itemName.getValue())
                renderText(heldItemMainhand, (float) (armorOffset - (durability.getValue() ? 10 : 2)), matrices);

            if (armor.getValue() || (durability.getValue() && heldItemMainhand.isDamageable()))
                xOffset += 16;

        }

        int size;
        for (int i2 = size = 3; i2 >= 0; i2 = --size) {
            ItemStack stack;

            if (!(stack = player.getInventory().getArmorStack(size)).isEmpty()) {
                int fixedEnchantOffset = getOffset(enchantOffset);

                if (armor.getValue()) {
                    int oldEnchantOffset = fixedEnchantOffset;
                    fixedEnchantOffset -= 32;
                    renderStack(stack, xOffset, oldEnchantOffset, enchantOffset, matrices, player);
                }

                if (durability.getValue() && stack.isDamageable())
                    renderDurability(stack, (float) xOffset, (float) fixedEnchantOffset, matrices);

                xOffset += 16;
            }
        }

        if (!offhand.isEmpty()) {
            int fixed = getOffset(enchantOffset);

            if (armor.getValue()) {
                int oldEnchantOffsetI = fixed;
                fixed -= 32;
                renderStack(offhand, xOffset, oldEnchantOffsetI, enchantOffset, matrices, player);
            }

            if (durability.getValue() && offhand.isDamageable()) {
                renderDurability(offhand, (float) xOffset, (float) fixed, matrices);
            }
        }

        matrices.pop();
    }

    private int getOffset(int offset) {
        int fixedOffset = armor.getValue() ? -26 : -27;
        
        if (offset > 4) {
            fixedOffset -= (offset - 4) * 8;
        }
        
        return fixedOffset;
    }

    private void renderStack(ItemStack stack, int x, int y, int enchHeight, MatrixStack matrix, PlayerEntity entity) {
        int height = (enchHeight > 4) ? ((enchHeight - 4) * 8 / 2) : 0;
        matrix.push();

        matrix.translate((float) x, (float) (y + height), 0.0f);
        matrix.translate(8.0f, 8.0f, 0.0f);
        matrix.scale(16.0f, 16.0f, 0.0f);

        matrix.multiplyPositionMatrix(new Matrix4f().scaling(1.0f, -1.0f, 0.0f));

        WorldRenderer.drawGuiItem(matrix, stack);
        mc.getBufferBuilders().getEntityVertexConsumers().draw();

        matrix.pop();

        if (stack.getCount() != 1) {
            FontManager.drawText(matrix, stack.getCount() + "", ((x + 19) - 2) - FontManager.getWidth(stack.getCount() + ""), y + 3 + FontManager.getHeight(stack.getCount() + ""), -1);
        }

        matrix.scale(0.5f, 0.5f, 0.5f);

        if (enchantNames.getValue())
            renderEnchants(stack, x, y - 24, matrix);

        matrix.scale(2.0f, 2.0f, 2.0f);

        if (entity.isUsingItem() && entity.getActiveItem().equals(stack) && stack.getItem().getEatSound() != null)
//            FontManager.drawText(matrix, "Eating...", x, y, 0xFFAA00);
            RenderUtils.drawRect(matrix, x + 3, y + 9, 15, 15, new Color(59, 179, 65, 148));
    }

    private void renderDurability(ItemStack stack, float x, float y, MatrixStack matrix) {
        int n = stack.getMaxDamage();
        int n2 = stack.getDamage();
        int percent = (int) ((n - n2) / (float) n * 100.0f);

        RenderSystem.disableDepthTest();
        matrix.scale(0.5f, 0.5f, 0.5f);

        FontManager.drawText(matrix, percent + "%", (int) (x * 2), (int) y, ColorUtil.hslToColor((n - n2) / (float) n * 120.0f, 100.0f, 50.0f, 1.0f).getRGB());

        matrix.scale(2.0f, 2.0f, 2.0f);

        RenderSystem.enableDepthTest();

    }

    private void renderText(ItemStack stack, float y, MatrixStack matrix) {
        matrix.scale(0.5f, 0.5f, 0.5f);

        RenderSystem.disableDepthTest();
        String name = stack.getName().getString();

        FontManager.drawText(matrix, name, -FontManager.getWidth(name) >> 1, (int) y, -1);

        RenderSystem.enableDepthTest();
        matrix.scale(2.0f, 2.0f, 2.0f);
    }


    private void renderEnchants(ItemStack stack, int xOffset, int yOffset, MatrixStack matrix) {
        Set<RegistryEntry<Enchantment>> e = EnchantmentHelper.getEnchantments(stack).getEnchantments();
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);

        ArrayList<String> enchantTexts = new ArrayList<>(e.size());
        for (RegistryEntry<Enchantment> enchantment : e) {
            enchantTexts.add(getEnchantText(enchantment) + enchantments.getLevel(enchantment));
        }

        for (String enchantment2 : enchantTexts) {
            if (enchantment2 != null) {
                FontManager.drawText(matrix, TextUtils.capitalize(enchantment2), xOffset * 2, yOffset, new Color(230, 230, 230).getRGB());
                yOffset += 8;
            }
        }

        if (stack.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE)) {
            FontManager.drawText(matrix, "God", xOffset * 2, yOffset, new Color(255, 85, 255).getRGB());
        }
    }

    public static String getEnchantText(RegistryEntry<Enchantment> enchantment) {
        String name = NametagsUtil.get(enchantment);
        return name.length() > 2 ? name.substring(0, 2) : name;
    }

    private String renderEntityName(PlayerEntity entityPlayer) {
        String s2 = entityPlayer.getDisplayName().getString();

        if (entityId.getValue()) {
            s2 = new StringBuilder().insert(0, s2).append(" ID: ").append(entityPlayer.getId()).toString();
        }

        if (gamemode.getValue()) {
            s2 = entityPlayer.isCreative() ? new StringBuilder().insert(0, s2).append(" [C]").toString() : (entityPlayer.isSpectator() ? new StringBuilder().insert(0, s2).append(" [I]").toString() : new StringBuilder().insert(0, s2).append(" [S]").toString());
        }

        if (ping.getValue() && (mc.getNetworkHandler() != null || !(entityPlayer == mc.player) && !mc.isInSingleplayer())) {
            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entityPlayer.getUuid());
            if (playerListEntry != null) {
                s2 = new StringBuilder().insert(0, s2).append(" ").append(getPingColor(getPlayerLatency(entityPlayer))).append(getPlayerLatency(entityPlayer)).append("ms").toString();
            }
        }

        if (!health.getValue()) {
            return s2;
        }

        String s22;
        double ceil = Math.ceil(getHealth(entityPlayer));

        if (ceil > 18) {
            s22 = Formatting.GREEN.toString();
        } else if (ceil > 16) {
            s22 = Formatting.DARK_GREEN.toString();
        } else if (ceil > 12) {
            s22 = Formatting.YELLOW.toString();
        } else if (ceil > 8) {
            s22 = Formatting.GOLD.toString();
        } else if (ceil > 5) {
            s22 = Formatting.RED.toString();
        } else {
            s22 = Formatting.DARK_RED.toString();
        }

        return new StringBuilder().insert(0, s2).append(s22).append(" ").append(ceil > 0.0 ? Integer.valueOf((int) ceil) : "0").toString();
    }

    public String getPingColor(double ping) {
        if (ColoredPing.getValue()) {
            if (ping <= 40.0) {
                return Formatting.GREEN + "";
            }
            if (ping <= 70.0) {
                return Formatting.DARK_GREEN + "";
            }
            if (ping <= 99.0) {
                return Formatting.YELLOW + "";
            }
            return Formatting.RED + "";
        }
        return "";
    }

    public static int getPlayerLatency(PlayerEntity player) {
        if (player == null) return 0;

        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        return playerListEntry == null ? 0 : playerListEntry.getLatency();
    }

    private Color getNameColor(PlayerEntity entityPlayer) {
        if (FriendManager.INSTANCE.isFriend(entityPlayer)) {
            return FriendsColor.getValue();
        }

        if (entityPlayer.isInvisible()) {
            return new Color(128, 128, 128);
        }

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entityPlayer.getUuid());
        if (playerListEntry == null) {
            return new Color(239, 1, 71);
        }

        if (entityPlayer.isSneaking()) {
            return SneakingColor.getValue();
        }

        return normalColor.getValue();
    }

    public float getHealth(PlayerEntity ent) {
        if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && healthCalc.getValue()) {
            ScoreboardObjective scoreBoard;
            String resolvedHp = "";
            if (ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null) {
                scoreBoard = ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                if (scoreBoard != null) {
                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);
                    MutableText text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreBoard.getNumberFormatOr(StyledNumberFormat.EMPTY));
                    resolvedHp = text2.getString();
                }
            }
            float numValue = 0.0f;
            try {
                numValue = Float.parseFloat(resolvedHp);
            }
            catch (NumberFormatException ignored) {
            }
            return numValue;
        }
        return ent.getHealth() + ent.getAbsorptionAmount();
    }
}
