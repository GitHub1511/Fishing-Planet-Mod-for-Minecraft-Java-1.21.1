package com.fishingplanet.fishingplanet.registry;

import com.fishingplanet.fishingplanet.FishingPlanetMod;
import com.fishingplanet.fishingplanet.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // Fish items (generated from manifest)
    public static final Item FISH_ITEM = register("fish", new FishItem(new Item.Settings().maxCount(1)));

    // Rod items
    public static final Item ROD_ITEM = register("rod", new RodItem(new Item.Settings().maxCount(1)));
    
    // Reel items
    public static final Item REEL_ITEM = register("reel", new ReelItem(new Item.Settings().maxCount(1)));
    
    // Line items
    public static final Item LINE_ITEM = register("line", new LineItem(new Item.Settings().maxCount(64)));
    
    // Hook items
    public static final Item HOOK_ITEM = register("hook", new HookItem(new Item.Settings().maxCount(64)));
    
    // Lure items
    public static final Item LURE_ITEM = register("lure", new LureItem(new Item.Settings().maxCount(16)));
    
    // Tackle Box
    public static final Item TACKLE_BOX = register("tackle_box", new TackleBoxItem(new Item.Settings().maxCount(1)));

    // Bobber
    public static final Item BOBBER_ITEM = register("bobber", new BobberItem(new Item.Settings().maxCount(1)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(FishingPlanetMod.MOD_ID, name), item);
    }

    public static void register() {
        // Add to creative tabs
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ROD_ITEM);
            entries.add(REEL_ITEM);
            entries.add(LURE_ITEM);
            entries.add(LINE_ITEM);
            entries.add(HOOK_ITEM);
            entries.add(TACKLE_BOX);
            entries.add(BOBBER_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(FISH_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(FISH_ITEM);
        });
    }
}