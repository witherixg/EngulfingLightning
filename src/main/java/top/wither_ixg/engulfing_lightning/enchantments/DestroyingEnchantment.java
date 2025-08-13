package top.wither_ixg.engulfing_lightning.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import static top.wither_ixg.engulfing_lightning.Keys.MAGNETIZED_ENCHANTMENT_NAME;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.ENGULFING_LIGHTNING_ITEM;

public class DestroyingEnchantment extends Enchantment {

    public DestroyingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.create(
                MAGNETIZED_ENCHANTMENT_NAME,
                item -> item.equals(ENGULFING_LIGHTNING_ITEM.get())
        ), new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
}
