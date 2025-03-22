package top.wither_ixg.engulfing_lightning;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Objects;

import static top.wither_ixg.engulfing_lightning.Main.MOD_ID;

public class ELDamageSources {
    public static final ResourceKey<DamageType> ENGULFING_LIGHTNING = ResourceKey.create(
            Registries.DAMAGE_TYPE, Objects.requireNonNull(ResourceLocation.tryBuild(MOD_ID, "engulfing_lightning")));

    public static DamageSource engulfingLightning(Level level, Entity causing, Entity direct) {
        return source(ENGULFING_LIGHTNING, level, causing, direct);
    }

    public static DamageSource source(ResourceKey<DamageType> key, Level level, Entity causing, Entity direct) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key), causing, direct);
    }
}
