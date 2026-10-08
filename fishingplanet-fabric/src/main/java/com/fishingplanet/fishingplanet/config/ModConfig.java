package com.fishingplanet.fishingplanet.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "fishingplanet")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public SpawnConfig spawn = new SpawnConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public FishingConfig fishing = new FishingConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public CompatibilityConfig compatibility = new CompatibilityConfig();

    public static class SpawnConfig {
        public float spawnRateMultiplier = 1.0f;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 200)
        public int maxFishPerChunk = 50;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public int minFishPerChunk = 5;

        public boolean enableNaturalSpawning = true;
        public boolean spawnInModdedBiomes = true;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 60)
        public int despawnTimeMinutes = 10;
    }

    public static class FishingConfig {
        public float catchChanceMultiplier = 1.0f;
        public boolean enableFishFighting = true;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public int tackleDurabilityLoss = 1;

        public float lineBreakChance = 0.1f;
        public float hookPulloutChance = 0.05f;
        public boolean enableRareCatches = true;
    }

    public static class CompatibilityConfig {
        public boolean enableIris = true;
        public boolean enableSodium = true;
        public boolean enableOptimumRealism = true;
        public boolean enableBliss = true;
        public boolean enableEMI = true;
        public boolean enableJEI = true;
        public boolean enableREI = true;
    }

    private static ModConfig INSTANCE;

    public static ModConfig get() {
        if (INSTANCE == null) {
            INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        }
        return INSTANCE;
    }

    public static void load() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void save() {
        if (INSTANCE != null) {
            AutoConfig.getConfigHolder(ModConfig.class).save();
        }
    }
}
