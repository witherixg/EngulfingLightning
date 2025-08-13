package top.wither_ixg.engulfing_lightning.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static top.wither_ixg.engulfing_lightning.registers.EnchantmentRegister.DESTROYING_ENCHANTMENT;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.ENGULFING_LIGHTNING_ITEM;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    void hurt(DamageSource p_32013_, float p_32014_, CallbackInfoReturnable<Boolean> cir){
        ItemStack stack = getItem();
        if (!stack.isEmpty() && stack.is(ENGULFING_LIGHTNING_ITEM.get()) && p_32013_.is(DamageTypeTags.IS_EXPLOSION)) {
            int destroyingLevel = stack.getEnchantmentLevel(DESTROYING_ENCHANTMENT.get());
            if (destroyingLevel >= 1) return;
            stack.enchant(DESTROYING_ENCHANTMENT.get(), 1);
            cir.setReturnValue(false);
        }
    }
}
