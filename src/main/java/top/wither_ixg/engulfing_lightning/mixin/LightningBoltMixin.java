package top.wither_ixg.engulfing_lightning.mixin;

import net.minecraft.world.entity.LightningBolt;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {

    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true, remap = true)
    public void spawnFire(CallbackInfo ci) {
        LightningBolt lightning = (LightningBolt) (Object) this;
        if(lightning.getTags().contains("fromEngulfingLightning")) ci.cancel();
    }
}
