package top.wither_ixg.engulfing_lightning;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MOD_ID)
public class Main {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "engulfing_lightning";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Registry - Sound
    private static RegistryObject<SoundEvent> build(String id) {
        return SOUND_EVENTS.register(id, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MOD_ID, id)));
    }

    public static final RegistryObject<SoundEvent> RAIDEN_1 = build("music_disc.raiden_1");
    public static final RegistryObject<SoundEvent> RAIDEN_2 = build("music_disc.raiden_2");

    // Registry - Item
    public static final RegistryObject<Item> ENGULFING_LIGHTNING_ITEM =
            ITEMS.register(MOD_ID,
                    EngulfingLightningItem::new);

    public static final RegistryObject<Item> MUSIC_DISC_RAIDEN_1_ITEM =
            ITEMS.register("music_disc_raiden_1",
                    () -> new RaidenRecordItem(RAIDEN_1, 209*20));

    public static final RegistryObject<Item> MUSIC_DISC_RAIDEN_2_ITEM =
            ITEMS.register("music_disc_raiden_2",
                    () -> new RaidenRecordItem(RAIDEN_2, 209*20));



    public Main() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SOUND_EVENTS.register(bus);

    }

}
