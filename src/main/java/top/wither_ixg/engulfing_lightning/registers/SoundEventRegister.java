package top.wither_ixg.engulfing_lightning.registers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.wither_ixg.engulfing_lightning.Main;

import static top.wither_ixg.engulfing_lightning.Main.MOD_ID;

public class SoundEventRegister {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);


    public static final RegistryObject<SoundEvent> RAIDEN_1 = build("music_disc.raiden_1");
    public static final RegistryObject<SoundEvent> RAIDEN_2 = build("music_disc.raiden_2");


    private static RegistryObject<SoundEvent> build(String id) {
        return SOUND_EVENTS.register(id, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MOD_ID, id)));
    }
}
