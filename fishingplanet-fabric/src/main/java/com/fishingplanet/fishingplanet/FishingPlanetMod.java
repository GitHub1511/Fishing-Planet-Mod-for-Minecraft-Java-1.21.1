package com.fishingplanet.fishingplanet;

import com.fishingplanet.fishingplanet.registry.ModItems;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import com.fishingplanet.fishingplanet.registry.ModSounds;
import com.fishingplanet.fishingplanet.fishing.FishingMechanics;
import com.fishingplanet.fishingplanet.loot.ModLootFunctions;
import com.fishingplanet.fishingplanet.spawn.FishSpawner;
import com.fishingplanet.fishingplanet.command.FPCommands;
import com.fishingplanet.fishingplanet.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingPlanetMod implements ModInitializer {
    public static final String MOD_ID = "fishingplanet";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Fishing Planet mod...");

        // Register items, entities, sounds
        ModItems.register();
        ModEntities.register();
        ModSounds.register();

        // Register fishing mechanics
        FishingMechanics.register();

        // Register loot injection (vanilla rods catch Fishing Planet fish)
        ModLootFunctions.register();

        // Register fish spawner
        FishSpawner.register();

        // Register commands
        CommandRegistrationCallback.EVENT.register(FPCommands::register);

        // Load config
        ModConfig.load();

        // Server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Fishing Planet: Server started, fish spawning enabled");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            FishSpawner.cleanup();
        });

        LOGGER.info("Fishing Planet mod initialized successfully!");
    }
}