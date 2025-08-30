package top.wither_ixg.engulfing_lightning.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;


public class RaidenRecordItem extends RecordItem {

    public RaidenRecordItem(Supplier<SoundEvent> soundSupplier, int lengthInTicks) {
        super(15,
                soundSupplier,
                new Item.Properties().rarity(Rarity.RARE).stacksTo(1),
                lengthInTicks);
    }
}
