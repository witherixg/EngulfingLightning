package top.wither_ixg.engulfing_lightning;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.lang.Math.*;
import static top.wither_ixg.engulfing_lightning.Main.LOGGER;


public class EngulfingLightningItem extends SwordItem {

    private static final Random random = new Random();

    public EngulfingLightningItem() {
        super(Tiers.NETHERITE, 3, -2.4F,
                (new Properties()).fireResistant().rarity(Rarity.EPIC));

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Server Side
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);

        // Get Entities
        ServerLevel serverLevel = (ServerLevel) level;
        List<Entity> entities = new ArrayList<>();
        serverLevel.getAllEntities().forEach(entities::add);
        entities.sort(Comparator.comparingDouble(entity -> entity.distanceTo(player)));

        entities.stream()
                .filter(entity -> entity instanceof Enemy)
                .filter(Entity::isAlive)
                .filter(entity -> entity.distanceTo(player) < 30)
                .limit(10).forEach(entity ->
                        summonLightning(serverLevel, player, entity, hand)
                );
        player.getCooldowns().addCooldown(this, 10);
        LOGGER.debug("on use!");
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack stack, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
    ) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.PASS;
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.PASS;
        summonLightning(((ServerLevel) level), player, target, hand);
        player.getCooldowns().addCooldown(this, 5);
        LOGGER.debug("on interact!");
        return InteractionResult.PASS;
    }

    public static void summonLightning(@NotNull ServerLevel level, @NotNull Player player, @NotNull Entity entity, @NotNull InteractionHand hand) {

        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        float base = lightning.getDamage();
        int enchantmentLevel = player.getItemInHand(hand).getEnchantmentLevel(Enchantments.SHARPNESS);
        float maxHealth = (float) Objects.requireNonNull(((LivingEntity) entity).getAttribute(Attributes.MAX_HEALTH)).getValue();
        int randInt = random.nextInt(5);
        lightning.setDamage(max(base + enchantmentLevel, (enchantmentLevel > randInt ? maxHealth / 3.0f : maxHealth / 6.0f)));

        lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
        lightning.setCause((ServerPlayer) player);
        lightning.addTag("fromEngulfingLightning");
        level.addFreshEntity(lightning);

        LOGGER.debug("Summoned lightning bolt at: {}", entity.getName());
    }
}
