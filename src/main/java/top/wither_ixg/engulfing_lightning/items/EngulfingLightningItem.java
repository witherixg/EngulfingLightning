package top.wither_ixg.engulfing_lightning.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.lang.Math.*;
import static net.minecraft.world.item.CreativeModeTabs.COMBAT;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static top.wither_ixg.engulfing_lightning.ELDamageSources.engulfingLightning;
import static top.wither_ixg.engulfing_lightning.Main.LOGGER;
import static top.wither_ixg.engulfing_lightning.event_handler.mod.CreativeTabHandler.add;


public class EngulfingLightningItem extends SwordItem {

    private static final Random random = new Random();

    public static final String LIGHTNING_TAG = "fromEngulfingLightning";
    public static final String HURT_TAG = "hurtByEngulfingLightning";

//    public static final String HURT_TAG = "hurtByEngulfingLightning";

    public EngulfingLightningItem() {
        super(Tiers.NETHERITE, 3, -2.4F,
                (new Properties()).fireResistant().rarity(Rarity.EPIC));
        add(COMBAT, this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Server Side
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);

        // Get Entities
        ServerLevel serverLevel = (ServerLevel) level;
        List<Entity> allEntities = new ArrayList<>();
        serverLevel.getAllEntities().forEach(allEntities::add);
        allEntities.sort(Comparator.comparingDouble(entity -> entity.distanceTo(player)));

        int knockBackLevel = getEnchantmentLevel(player, hand, KNOCKBACK);
        int sweepingLevel = getEnchantmentLevel(player, hand, SWEEPING_EDGE);

        allEntities.stream()
                .filter(entity -> entity instanceof Enemy)
                .filter(entity -> entity instanceof LivingEntity)
                .filter(Entity::isAlive)
                .filter(entity -> entity.distanceTo(player) < (30 + 15 * knockBackLevel))
                .limit(10L * (1 + sweepingLevel)).forEach(
                entity -> {
                    summonLightning(serverLevel, player, entity, hand);
                    // Damage the item
                    stack.hurtAndBreak(1, player,
                            p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND)
                    );
                }
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

    private static void summonLightning(@NotNull ServerLevel level, @NotNull Player player, @NotNull Entity entity, @NotNull InteractionHand hand) {

        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        float base = 5.0F;
        int enchantmentLevel = getEnchantmentLevel(player, hand, SHARPNESS);
        float maxHealth = (float) Objects.requireNonNull(((LivingEntity) entity).getAttribute(Attributes.MAX_HEALTH)).getValue();
        int randInt = random.nextInt(8);
        float damage = max(2 * (base + enchantmentLevel), (enchantmentLevel > randInt ? maxHealth / 10.0f : maxHealth / 20.0f));
        // Clear lightning damage
        lightning.setDamage(0);
        // Considered as player damage
        entity.addTag(HURT_TAG);
        entity.hurt(engulfingLightning(level, entity, player), damage);
        entity.removeTag(HURT_TAG);
        lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
        lightning.setCause((ServerPlayer) player);
        lightning.addTag(LIGHTNING_TAG);
        level.addFreshEntity(lightning);

        LOGGER.debug("Summoned lightning bolt at: {}", entity.getName());
    }

    private static int getEnchantmentLevel(Player player, InteractionHand hand, Enchantment enchantment) {
        return player.getItemInHand(hand).getEnchantmentLevel(enchantment);
    }
}
