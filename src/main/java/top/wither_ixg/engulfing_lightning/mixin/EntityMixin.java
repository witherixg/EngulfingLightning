package top.wither_ixg.engulfing_lightning.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static top.wither_ixg.engulfing_lightning.items.EngulfingLightningItem.LIGHTNING_TAG;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    void preventFireAspect(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        if(lightningBolt.getTags().contains(LIGHTNING_TAG)) {
            ci.cancel();
        }
    }
}
