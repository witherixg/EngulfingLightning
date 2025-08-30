package top.wither_ixg.engulfing_lightning.items;

import static net.minecraft.world.item.CreativeModeTabs.COMBAT;
import static net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES;
import static top.wither_ixg.engulfing_lightning.event_handler.mod.CreativeTabHandler.add;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.*;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.MUSIC_DISC_RAIDEN_1_ITEM;
import static top.wither_ixg.engulfing_lightning.registers.ItemRegister.MUSIC_DISC_RAIDEN_2_ITEM;

public class CreativeTabList {
    public static void init(){
        add(COMBAT, ENGULFING_LIGHTNING_ITEM.get());
        add(TOOLS_AND_UTILITIES, MUSIC_DISC_RAIDEN_1_ITEM.get());
        add(TOOLS_AND_UTILITIES, MUSIC_DISC_RAIDEN_2_ITEM.get());
    }
}
