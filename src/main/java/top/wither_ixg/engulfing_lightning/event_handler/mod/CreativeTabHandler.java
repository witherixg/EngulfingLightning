package top.wither_ixg.engulfing_lightning.event_handler.mod;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(bus = MOD)
public class CreativeTabHandler {

    private static final Map<ResourceKey<CreativeModeTab>, List<Item>> MAP = new HashMap<>();


    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        List<Item> items = get(event.getTabKey());
        if (items != null) {
            items.forEach(event::accept);
        }
    }

    public static void add(ResourceKey<CreativeModeTab> tab, Item item) {
        if(!MAP.containsKey(tab)) MAP.put(tab, new ArrayList<>());
        MAP.get(tab).add(item);
    }

    @Nullable
    public static List<Item> get(ResourceKey<CreativeModeTab> tab) {
        return MAP.get(tab);
    }
}
