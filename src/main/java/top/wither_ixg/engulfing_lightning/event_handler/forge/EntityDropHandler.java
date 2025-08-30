package top.wither_ixg.engulfing_lightning.event_handler.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static top.wither_ixg.engulfing_lightning.Main.LOGGER;
import static top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem.HURT_TAG;
import static top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem.LIGHTNING_VIA_USE;
import static top.wither_ixg.engulfing_lightning.registers.EnchantmentRegister.DESTROYING_ENCHANTMENT;
import static top.wither_ixg.engulfing_lightning.registers.EnchantmentRegister.MAGNETIZED_ENCHANTMENT;

@Mod.EventBusSubscriber(bus = FORGE)
public class EntityDropHandler {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (notMagnetized(event)) return;
        Player player = (Player) event.getEntity().getLastAttacker();
        int level = player.getMainHandItem().getEnchantmentLevel(DESTROYING_ENCHANTMENT.get());
        Collection<ItemEntity> drops = event.getDrops();
        if (event.getEntity().getTags().contains(LIGHTNING_VIA_USE) && level != 0) {
            drops.forEach(Entity::kill);
        }
        drops.forEach(itemEntity -> {
            itemEntity.setPos(player.position());
            itemEntity.setPickUpDelay(0);
            LOGGER.debug("Reset pos: {}", itemEntity);
        });
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (notMagnetized(event)) return;
        event.setCanceled(true);
        Player player = (Player) event.getEntity().getLastAttacker();
        Level level = player.getCommandSenderWorld();
        level.addFreshEntity(new ExperienceOrb(
                level,
                player.getX(),
                player.getY(),
                player.getZ(),
                event.getDroppedExperience()));
    }


    private static boolean notMagnetized(LivingEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getTags().contains(HURT_TAG)) return true;
        if (!(entity.getLastAttacker() instanceof Player player)) return true;
        ItemStack stack = player.getMainHandItem();
        int enchantmentLevel = stack.getEnchantmentLevel(MAGNETIZED_ENCHANTMENT.get());
        return enchantmentLevel == 0;
    }
}
