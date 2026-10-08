package com.fishingplanet.fishingplanet.registry;

import com.fishingplanet.fishingplanet.FishingPlanetMod;

import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.entity.bobber.FishingPlanetBobberEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FishingPlanetFishEntity> FISHING_PLANET_FISH = register(
        "fishing_planet_fish",
        FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, FishingPlanetFishEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 0.5f))
            .trackRangeBlocks(64)
            .trackedUpdateRate(20)
            .build()
    );

    public static final EntityType<FishingPlanetBobberEntity> FISHING_PLANET_BOBBER = register(
        "fishing_planet_bobber",
        FabricEntityTypeBuilder.<FishingPlanetBobberEntity>create(SpawnGroup.MISC, FishingPlanetBobberEntity::new)
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
            .trackRangeBlocks(64)
            .trackedUpdateRate(20)
            .build()
    );

    private static <T extends net.minecraft.entity.Entity> EntityType<T> register(String name, EntityType<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(FishingPlanetMod.MOD_ID, name), type);
    }

    public static void register() {
        // Register attributes for fish entity (bobbers need no attributes)
        FabricDefaultAttributeRegistry.register(FISHING_PLANET_FISH, FishingPlanetFishEntity.createFishAttributes());
    }
}