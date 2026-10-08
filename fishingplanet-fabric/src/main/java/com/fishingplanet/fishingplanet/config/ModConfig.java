package com.fishingplanet.fishingplanet.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public SpawnConfig spawn = new SpawnConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public FishingConfig fishing = new FishingConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public CompatibilityConfig compatibility = new CompatibilityConfig();

    public static class SpawnConfig {
        @Comment("Global fish spawn rate multiplier (0.1 - 10.0)")
        @ConfigEntry.BoundedDiscrete(min = "0.1", max = "10.0")
        public float spawnRateMultiplier = 1.0f;

        @Comment("Maximum fish per chunk")
        @ConfigEntry.BoundedDiscrete(min = "1", max = "200")
        public int maxFishPerChunk = 50;

        @Comment("Minimum fish per chunk")
        @ConfigEntry.BoundedDiscrete(min = "0", max = "50")
        public int minFishPerChunk = 5;

        @Comment("Enable natural fish spawning in all water bodies")
        public boolean enableNaturalSpawning = true;

        @Comment("Spawn fish in modded biomes (BOP, Terralith, etc.)")
        public boolean spawnInModdedBiomes = true;

        @Comment("Fish despawn time in minutes (0 = never)")
        @ConfigEntry.BoundedDiscrete(min = "0", max = "60")
        public int despawnTimeMinutes = 10;
    }

    public static class FishingConfig {
        @Comment("Base catch chance multiplier")
        @ConfigEntry.BoundedDiscrete(min = "0.1", max = "5.0")
        public float catchChanceMultiplier = 1.0f;

        @Comment("Enable fish fighting mini-game")
        public boolean enableFishFighting = true;

        @Comment("Tackle durability loss on catch")
        @ConfigEntry.BoundedDiscrete(min = "0", max = "10")
        public int tackleDurabilityLoss = 1;

        @Comment("Line break chance on big fish (0.0 - 1.0)")
        @ConfigEntry.BoundedDiscrete(min = "0.0", max = "1.0")
        public float lineBreakChance = 0.1f;

        @Comment("Hook pull-out chance (0.0 - 1.0)")
        @ConfigEntry.BoundedDiscrete(min = "0.0", max = "1.0")
        public float hookPulloutChance = 0.05f;

        @Comment("Enable rare/legendary fish catches")
        public boolean enableRareCatches = true;
    }

    public static class CompatibilityConfig {
        @Comment("Enable Iris shader compatibility")
        public boolean enableIris = true;

        @Comment("Enable Sodium optimization compatibility")
        public boolean enableSodium = true;

        @Comment("Enable Optimum Realism PBR textures")
        public boolean enableOptimumRealism = true;

        @Comment("Enable Bliss shader custom uniforms")
        public boolean enableBliss = true;

        @Comment("Enable EMI integration")
        public boolean enableEMI = true;

        @Comment("Enable JEI integration")
        public boolean enableJEI = true;

        @Comment("Enable REI integration")
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