package top.wither_ixg.engulfing_lightning;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import top.wither_ixg.engulfing_lightning.enchantments.MagnetizedEnchantment;
import top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem;
import top.wither_ixg.engulfing_lightning.items.RaidenRecordItem;

import static top.wither_ixg.engulfing_lightning.Keys.*;
import static top.wither_ixg.engulfing_lightning.registers.EnchantmentRegister.ENCHANTMENTS;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.ITEMS;
import static top.wither_ixg.engulfing_lightning.registers.SoundEventRegister.SOUND_EVENTS;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MOD_ID)
public class Main {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "engulfing_lightning";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SOUND_EVENTS.register(bus);
        ENCHANTMENTS.register(bus);
    }

}
