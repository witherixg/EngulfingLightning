package top.wither_ixg.engulfing_lightning.registers;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.wither_ixg.engulfing_lightning.enchantments.MagnetizedEnchantment;

import static top.wither_ixg.engulfing_lightning.Keys.MAGNETIZED_ENCHANTMENT_NAME;
import static top.wither_ixg.engulfing_lightning.Main.MOD_ID;

public class EnchantmentRegister {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);


    public static final RegistryObject<Enchantment> MAGNETIZED_ENCHANTMENT =
            ENCHANTMENTS.register(MAGNETIZED_ENCHANTMENT_NAME,
                    MagnetizedEnchantment::new);

}
