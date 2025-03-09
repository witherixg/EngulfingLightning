package top.wither_ixg.engulfing_lightning;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RaidenRecordItem extends RecordItem {

    public RaidenRecordItem(Supplier<SoundEvent> soundSupplier, int lengthInTicks) {
        super(15,
                soundSupplier,
                new Item.Properties().rarity(Rarity.RARE).stacksTo(1),
                lengthInTicks);

    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext p_43048_) {
        InteractionResult result = super.useOn(p_43048_);
        Main.LOGGER.info("useOn!");
        return result;
    }
}
