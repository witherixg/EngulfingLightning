package top.wither_ixg.engulfing_lightning;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static net.minecraft.world.item.Items.NETHERITE_SWORD;
import static top.wither_ixg.engulfing_lightning.Main.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // netherite_sword -> engulfing_lightning
        // disc -> raiden_1
        // raiden_1 -> raiden_2
        Level level = event.getLevel();
        if (level.isClientSide) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LightningBolt)) return;
        // Get nearby items
        List<ItemEntity> items = level.getEntitiesOfClass(
                ItemEntity.class,
                new AABB(entity.getX() - 1, entity.getY() - 1, entity.getZ() - 1,
                        entity.getX() + 1, entity.getY() + 1, entity.getZ() + 1)
        );

        items.stream().filter(itemEntity -> itemEntity.getItem().is(NETHERITE_SWORD))
                .forEach(itemEntity -> {
                    // netherite_sword -> engulfing_lightning
                    convert(itemEntity, ENGULFING_LIGHTNING_ITEM.get());
                });

        items.stream().filter(itemEntity -> itemEntity.getItem().getItem() instanceof RecordItem)
                .forEach(itemEntity -> {
                    Item item = itemEntity.getItem().getItem();
                    if (!(item instanceof RaidenRecordItem)) {
                        // disc -> raiden_1
                        convert(itemEntity, MUSIC_DISC_RAIDEN_1_ITEM.get());
                    } else {
                        // raiden_1 -> raiden_2
                        convert(itemEntity, MUSIC_DISC_RAIDEN_2_ITEM.get());
                    }
                });
    }

    private static void convert(ItemEntity itemEntity, Item newItem) {

        itemEntity.setInvulnerable(true);
        ItemStack originalStack = itemEntity.getItem();
        ItemStack newStack = new ItemStack(newItem);

        if (originalStack.hasTag() && originalStack.getTag() != null) {
            newStack.setTag(originalStack.getTag().copy());
        }

        if (originalStack.isDamaged()) {
            newStack.setDamageValue(originalStack.getDamageValue());
        }

        itemEntity.setItem(newStack);
        LOGGER.debug("Converted!");
    }
}
