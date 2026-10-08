package com.fishingplanet.fishingplanet.loot;

import com.fishingplanet.fishingplanet.registry.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModLootFunctions {
    public static final LootFunctionType<SetFishCatchFunction> SET_FISH_CATCH = Registry.register(
        Registries.LOOT_FUNCTION_TYPE,
        Identifier.of("fishingplanet", "set_fish_catch"),
        new LootFunctionType<>(SetFishCatchFunction.CODEC));

    private static final Identifier FISHING_FISH_ID = Identifier.of("minecraft", "gameplay/fishing/fish");

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin() && FISHING_FISH_ID.equals(key.getValue())) {
                tableBuilder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(ModItems.FISH_ITEM)
                        .weight(50)
                        .apply(SetFishCatchFunction.builder())
                        .build())
                    .build());
            }
        });
    }
}
