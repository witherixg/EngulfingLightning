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

import static net.minecraft.world.item.CreativeModeTabs.COMBAT;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static top.wither_ixg.engulfing_lightning.ELDamageSources.engulfingLightning;
import static top.wither_ixg.engulfing_lightning.Main.LOGGER;
import static top.wither_ixg.engulfing_lightning.event_handler.forge.AttackEntityHandler.attackCount;
import static top.wither_ixg.engulfing_lightning.event_handler.mod.CreativeTabHandler.add;


public class EngulfingLightningItem extends SwordItem {

    public static final String LIGHTNING_TAG = "fromEngulfingLightning";
    public static final String HURT_TAG = "hurtByEngulfingLightning";
    public static final String LIGHTNING_VIA_USE = "lightningViaUse";

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

        allEntities.stream()
                .filter(entity -> entity instanceof Enemy)
                .filter(entity -> entity instanceof LivingEntity)
                .filter(Entity::isAlive)
                .filter(entity -> entity.distanceTo(player) < (30 + 15 * knockBackLevel))
                .limit(10L * (1 + sweepingLevel)).forEach(
                        entity -> {
                            summonLightning(player, (LivingEntity) entity, stack, true);
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
        attackCount.replace(target, attackCount.getOrDefault(target, 0) + 1);
        LOGGER.debug("{} attackCount: {}", target, attackCount.get(target));
        return super.hurtEnemy(stack, target, attacker);
    }

    public static void summonLightning(@NotNull Entity attacker, @NotNull LivingEntity entity, ItemStack stack, boolean viaUse) {

        ServerLevel level = (ServerLevel) attacker.level();

        int sharpnessLevel = stack.getEnchantmentLevel(SHARPNESS);
        int lootingLevel = stack.getEnchantmentLevel(MOB_LOOTING);

        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        float base = 5.0F;
        float maxHealth = (float) Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).getValue();
        float currHealth = entity.getHealth();
        float damage = 2 * (base + 1.5f * sharpnessLevel);

        if (currHealth <= maxHealth * lootingLevel / 8) {
            damage *= 10;
        }

        // Clear lightning damage
        lightning.setDamage(0);
        // Considered as player damage
        entity.addTag(HURT_TAG);
        if (viaUse) entity.addTag(LIGHTNING_VIA_USE);
        entity.hurt(engulfingLightning(level, entity, attacker), damage);
        entity.removeTag(HURT_TAG);
        if (viaUse) entity.removeTag(LIGHTNING_VIA_USE);
        lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
        lightning.setCause((ServerPlayer) attacker);
        lightning.addTag(LIGHTNING_TAG);
        level.addFreshEntity(lightning);
    }

}
