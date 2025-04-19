package top.wither_ixg.engulfing_lightning.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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

    private static final Map<Entity, Integer> attackCount = new HashMap<>();

    private static final Map<Entity, Integer> lastHurtTime = new HashMap<>();


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

        int knockBackLevel = stack.getEnchantmentLevel(KNOCKBACK);
        int sweepingLevel = stack.getEnchantmentLevel(SWEEPING_EDGE);
        int sharpnessLevel = stack.getEnchantmentLevel(SHARPNESS);

        allEntities.stream()
                .filter(entity -> entity instanceof Enemy)
                .filter(entity -> entity instanceof LivingEntity)
                .filter(Entity::isAlive)
                .filter(entity -> entity.distanceTo(player) < (30 + 15 * knockBackLevel))
                .limit(10L * (1 + sweepingLevel)).forEach(
                        entity -> {
                            summonLightning(player, entity, sharpnessLevel);
                            // Damage the item
                            stack.hurtAndBreak(1, player,
                                    p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND)
                            );
                        }
                );

        player.getCooldowns().addCooldown(this, 200);
        LOGGER.debug("on use!");
        return InteractionResultHolder.pass(stack);
    }


    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        int enchantmentLevel = stack.getEnchantmentLevel(SHARPNESS);

        attackCount.putIfAbsent(target, 2);
        int count = attackCount.get(target) + 1;

        int hurtTime = target.getLastHurtByMobTimestamp();
        lastHurtTime.putIfAbsent(target, hurtTime);

        if (count == 3) {
            summonLightning(attacker, target, enchantmentLevel);
            attackCount.replace(target, 0);
            lastHurtTime.replace(target, hurtTime);
            LOGGER.debug("Summoned: 3 hits");
        } else attackCount.replace(target, count);

        int delta = hurtTime - lastHurtTime.get(target);
        if (delta >= 50) {
            summonLightning(attacker, target, enchantmentLevel);
            attackCount.replace(target, 0);
            lastHurtTime.replace(target, hurtTime);
            LOGGER.debug("Summoned: cd ({} tick) > 2.5s", delta);
        }
        return result;
    }

    private static void summonLightning(@NotNull Entity attacker, @NotNull Entity entity, int sharpnessLevel) {

        ServerLevel level = (ServerLevel) attacker.level();

        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        float base = 5.0F;
        float maxHealth = (float) Objects.requireNonNull(((LivingEntity) entity).getAttribute(Attributes.MAX_HEALTH)).getValue();
        int randInt = random.nextInt(8);
        float damage = max(2 * (base + 1.5f * sharpnessLevel), (sharpnessLevel > randInt ? maxHealth / 10.0f : maxHealth / 20.0f));
        // Clear lightning damage
        lightning.setDamage(0);
        // Considered as player damage
        entity.addTag(HURT_TAG);
        entity.hurt(engulfingLightning(level, entity, attacker), damage);
        entity.removeTag(HURT_TAG);
        lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
        lightning.setCause((ServerPlayer) attacker);
        lightning.addTag(LIGHTNING_TAG);
        level.addFreshEntity(lightning);
    }
}
