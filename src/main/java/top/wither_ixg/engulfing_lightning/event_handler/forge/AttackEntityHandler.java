package top.wither_ixg.engulfing_lightning.event_handler.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

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
        if (!entity.isAlive()) return;
        LivingEntity target = (LivingEntity) entity;
        Level level = attacker.level();
        if (level.isClientSide) return;
        LOGGER.debug("{} attacked {}", attacker.getName(), target.getName());
        ItemStack stack = attacker.getMainHandItem();
        if (!stack.getItem().equals(ENGULFING_LIGHTNING_ITEM.get())) {
            return;
        }

        int enchantmentLevel = stack.getEnchantmentLevel(SHARPNESS);

        attackCount.putIfAbsent(target, 2);
        int count = attackCount.get(target);

        int hurtTime = target.getLastHurtByMobTimestamp();
        lastHurtTime.putIfAbsent(target, hurtTime);

        if (count == 2) {
            summonLightning(attacker, target, enchantmentLevel);
            attackCount.replace(target, 0);
            lastHurtTime.replace(target, hurtTime);
            LOGGER.debug("Summoned: 3 hits");
        }

        int delta = target.tickCount - lastHurtTime.get(target);
        LOGGER.debug("delta: {}", delta);
        if (delta >= 50) {
            summonLightning(attacker, target, enchantmentLevel);
            attackCount.replace(target, 0);
            lastHurtTime.replace(target, hurtTime);
            LOGGER.debug("Summoned: cd ({} tick) > 2.5s", delta);
        }
    }
}
