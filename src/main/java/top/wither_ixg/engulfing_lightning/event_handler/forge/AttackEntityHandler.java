package top.wither_ixg.engulfing_lightning.event_handler.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.item.enchantment.Enchantments.SHARPNESS;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static top.wither_ixg.engulfing_lightning.Main.LOGGER;
import static top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem.summonLightning;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.ENGULFING_LIGHTNING_ITEM;

@Mod.EventBusSubscriber(bus = FORGE)
public class AttackEntityHandler {

    public static final Map<Entity, Integer> attackCount = new HashMap<>();

    public static final Map<Entity, Integer> lastHurtTime = new HashMap<>();


    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        Entity entity = event.getTarget();
        if(entity instanceof PartEntity<?> part)
            entity = part.getParent();

        if (!entity.isAlive()) return;

        if(!(entity instanceof LivingEntity target)) return;

        Level level = attacker.level();
        if (level.isClientSide) return;
        LOGGER.debug("{} attacked {}(uuid: {})", attacker.getName(), target.getName(), target.getUUID());
        ItemStack stack = attacker.getMainHandItem();
        if (!stack.getItem().equals(ENGULFING_LIGHTNING_ITEM.get())) {
            return;
        }

        int enchantmentLevel = stack.getEnchantmentLevel(SHARPNESS);

        attackCount.putIfAbsent(target, 3);
        int count = attackCount.get(target);

        int hurtTime = target.getLastHurtByMobTimestamp();
        lastHurtTime.putIfAbsent(target, hurtTime);
        int attackTime = target.tickCount;

        int delta = attackTime - lastHurtTime.get(target);
        LOGGER.debug("delta: {}", delta);

        LOGGER.debug("delta: {}", delta);
        if (count == 3 || delta >= 50) {
            Objects.requireNonNull(level.getServer()).execute(() -> {
                if (target.isAlive()) summonLightning(attacker, target, enchantmentLevel);
            });
            attackCount.replace(target, 0);
            lastHurtTime.replace(target, attackTime);
            if (count == 2) LOGGER.debug("Summoned: 3 hits");
            if (delta >= 50) LOGGER.debug("Summoned: cd ({} tick) >= 2.5s", delta);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        attackCount.remove(entity);
        lastHurtTime.remove(entity);
    }
}
