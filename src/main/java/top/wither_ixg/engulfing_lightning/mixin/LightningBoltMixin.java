package top.wither_ixg.engulfing_lightning.mixin;

import net.minecraft.world.entity.LightningBolt;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static top.wither_ixg.engulfing_lightning.EngulfingLightningItem.LIGHTNING_TAG;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {

    @Shadow
    private int life;

    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    void preventFire(CallbackInfo ci) {
        LightningBolt lightning = (LightningBolt) (Object) this;
        if (lightning.getTags().contains(LIGHTNING_TAG)) ci.cancel();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LightningBolt;life:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0
            )
    )
    void reduceLife(LightningBolt instance, int value) {
        this.life -= 1;
    }

}
