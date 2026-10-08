package com.fishingplanet.fishingplanet.spawn;

import com.fishingplanet.fishingplanet.config.ModConfig;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FishSpawner {
    private static final Map<ChunkPos, Integer> chunkFishCount = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> fishSpawnTimes = new ConcurrentHashMap<>();
    private static boolean enabled = true;

    public static void register() {
        // Register spawn logic
    }

    public static void onChunkLoad(ServerWorld world, Chunk chunk) {
        if (!enabled) return;
        
        ChunkPos chunkPos = chunk.getPos();
        int currentCount = chunkFishCount.getOrDefault(chunkPos, 0);
        int maxFish = ModConfig.get().maxFishPerChunk;
        
        if (currentCount < maxFish) {
            spawnFishInChunk(world, chunkPos, maxFish - currentCount);
        }
    }

    public static void onChunkUnload(ChunkPos chunkPos) {
        chunkFishCount.remove(chunkPos);
    }

    public static void spawnFishInChunk(ServerWorld world, ChunkPos chunkPos, int count) {
        if (!enabled) return;
        if (count <= 0) return;

        Biome biome = world.getBiome(chunkPos.getCenterBlockPos()).value();
        int spawned = 0;
        
        for (int i = 0; i < count && spawned < count; i++) {
            BlockPos spawnPos = findWaterPosition(world, chunkPos);
            if (spawnPos == null) break;
            
            float depth = calculateWaterDepth(world, spawnPos);
            FishSpecies species = selectSpeciesForBiome(world, biome, depth);
            
            if (species != null) {
                FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
                fish.setSpecies(species);
                fish.refreshPositionAndAngles(
                    spawnPos.getX() + 0.5, 
                    spawnPos.getY() + 1, 
                    spawnPos.getZ() + 0.5, 
                    world.random.nextFloat() * 360, 
                    0
                );
                
                // Check spawn conditions
                if (canSpawnHere(world, spawnPos, species, depth)) {
                    world.spawnEntity(fish);
                    fishSpawnTimes.put(fish.getUuid(), world.getTime());
                    chunkFishCount.merge(chunkPos, 1, Integer::sum);
                    spawned++;
                }
            }
        }
    }

    private static BlockPos findWaterPosition(ServerWorld world, ChunkPos chunkPos) {
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        
        for (int attempts = 0; attempts < 50; attempts++) {
            int x = startX + world.random.nextInt(16);
            int z = startZ + world.random.nextInt(16);
            int y = world.getTopY() - 1;
            
            // Find water surface
            while (y > world.getBottomY()) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = world.getBlockState(pos);
                
                if (state.isOf(Blocks.WATER)) {
                    // Check if there's air above
                    BlockPos above = pos.up();
                    if (world.getBlockState(above).isAir()) {
                        return pos;
                    }
                } else if (state.isAir()) {
                    // Found air, water must be below
                    break;
                }
                y--;
            }
        }
        return null;
    }

    private static float calculateWaterDepth(ServerWorld world, BlockPos pos) {
        float depth = 0;
        BlockPos checkPos = pos.down();
        
        while (checkPos.getY() > world.getBottomY()) {
            BlockState state = world.getBlockState(checkPos);
            if (state.isOf(Blocks.WATER)) {
                depth++;
                checkPos = checkPos.down();
            } else {
                break;
            }
        }
        return depth;
    }

    private static FishSpecies selectSpeciesForBiome(ServerWorld world, Biome biome, float depth) {
        java.util.List<FishSpecies> candidates = FishSpecies.getByHabitat(biome.getCategory().name());
        
        // Filter by depth
        candidates = candidates.stream()
            .filter(s -> matchesDepth(s.depth(), depth))
            .toList();
        
        if (candidates.isEmpty()) {
            candidates = FishSpecies.getAll();
        }
        
        // Weighted random
        int totalWeight = candidates.stream().mapToInt(s -> s.spawnWeight()).sum();
        if (totalWeight <= 0) return null;
        
        int roll = world.random.nextInt(totalWeight);
        for (FishSpecies species : candidates) {
            roll -= species.spawnWeight();
            if (roll < 0) return species;
        }
        
        return candidates.get(0);
    }

    private static boolean matchesDepth(String speciesDepth, float depth) {
        return switch (speciesDepth.toLowerCase()) {
            case "shallow" -> depth < 3;
            case "mid" -> depth >= 3 && depth < 10;
            case "bottom" -> depth >= 10;
            case "top" -> depth < 2;
            case "pelagic" -> depth > 20;
            case "shallow-mid" -> depth < 8;
            case "mid-top" -> depth > 2 && depth < 15;
            case "mid-bottom" -> depth > 5;
            case "various" -> true;
            default -> true;
        };
    }

    private static boolean canSpawnHere(ServerWorld world, BlockPos pos, FishSpecies species, float depth) {
        // Check light level
        if (world.getLightLevel(pos) < species.minLightLevel()) return false;
        
        // Check temperature
        float temp = biome.getTemperature(pos);
        if (temp < species.minTemperature() || temp > species.maxTemperature()) return false;
        
        // Check population cap
        ChunkPos chunkPos = new ChunkPos(pos);
        int current = chunkFishCount.getOrDefault(chunkPos, 0);
        if (current >= ModConfig.get().maxFishPerChunk) return false;
        
        return true;
    }

    public static void cleanup() {
        chunkFishCount.clear();
        fishSpawnTimes.clear();
    }

    public static void setEnabled(boolean enabled) {
        FishSpawner.enabled = enabled;
    }

    public static int getChunkFishCount(ChunkPos chunkPos) {
        return chunkFishCount.getOrDefault(chunkPos, 0);
    }

    public static void onFishDeath(ServerWorld world, FishingPlanetFishEntity fish) {
        ChunkPos chunkPos = new ChunkPos(fish.getBlockPos());
        chunkFishCount.compute(chunkPos, (k, v) -> v == null || v <= 1 ? null : v - 1);
        fishSpawnTimes.remove(fish.getUuid());
    }
}