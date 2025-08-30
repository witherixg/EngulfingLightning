package top.wither_ixg.engulfing_lightning.registers;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem;
import top.wither_ixg.engulfing_lightning.items.RaidenRecordItem;

import static top.wither_ixg.engulfing_lightning.Keys.*;
import static top.wither_ixg.engulfing_lightning.Main.*;
import static top.wither_ixg.engulfing_lightning.registers.SoundEventRegister.RAIDEN_1;
import static top.wither_ixg.engulfing_lightning.registers.SoundEventRegister.RAIDEN_2;

public class ItemRegister {


    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);


    public static final RegistryObject<Item> ENGULFING_LIGHTNING_ITEM =
            ITEMS.register(ENGULFING_LIGHTNING_ITEM_NAME,
                    EngulfingLightningItem::new);

    public static final RegistryObject<Item> MUSIC_DISC_RAIDEN_1_ITEM =
            ITEMS.register(RAIDEN_RECORD_ITEM_1_NAME,
                    () -> new RaidenRecordItem(RAIDEN_1, 209*20));

    public static final RegistryObject<Item> MUSIC_DISC_RAIDEN_2_ITEM =
            ITEMS.register(RAIDEN_RECORD_ITEM_2_NAME,
                    () -> new RaidenRecordItem(RAIDEN_2, 209*20));

}
