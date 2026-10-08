package com.fishingplanet.fishingplanet.spawn;

import com.fishingplanet.fishingplanet.config.ModConfig;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.fishing.FishingMechanics;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FishSpawner {
    private static final AtomicInteger ACTIVE_FISH = new AtomicInteger(0);
    private static boolean enabled = true;

    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register(FishSpawner::onChunkLoad);
    }

    public static void onChunkLoad(ServerWorld world, Chunk chunk) {
        if (!enabled || !ModConfig.get().spawn.enableNaturalSpawning) {
            return;
        }
        if (ACTIVE_FISH.get() >= ModConfig.get().spawn.maxFishTotal) {
            return;
        }
        ChunkPos chunkPos = chunk.getPos();
        int max = (int) (ModConfig.get().spawn.maxFishPerChunk * ModConfig.get().spawn.spawnRateMultiplier);
        if (countFishInChunk(world, chunkPos) < max) {
            spawnFishInChunk(world, chunkPos, max);
        }
    }

    public static void spawnFishInChunk(ServerWorld world, ChunkPos chunkPos, int max) {
        if (!enabled || max <= 0) {
            return;
        }
        for (int i = 0; i < max; i++) {
            if (ACTIVE_FISH.get() >= ModConfig.get().spawn.maxFishTotal) {
                return;
            }
            if (countFishInChunk(world, chunkPos) >= max) {
                return;
            }
            BlockPos water = findWaterPosition(world, chunkPos);
            if (water == null) {
                return;
            }
            float depth = calculateWaterDepth(world, water);
            FishSpecies species = selectSpecies(world, depth);
            if (species == null) {
                continue;
            }
            FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
            fish.setSpecies(species);
            fish.refreshPositionAndAngles(
                water.getX() + 0.5, water.getY() + 1.0, water.getZ() + 0.5,
                world.random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(fish);
            ACTIVE_FISH.incrementAndGet();
        }
    }

    public static int countFishInChunk(ServerWorld world, ChunkPos chunkPos) {
        Box box = new Box(chunkPos.getStartX(), -64.0, chunkPos.getStartZ(),
            chunkPos.getStartX() + 16.0, 320.0, chunkPos.getStartZ() + 16.0);
        return world.getEntitiesByClass(FishingPlanetFishEntity.class, box, Entity::isAlive).size();
    }

    public static void onFishRemoved() {
        ACTIVE_FISH.updateAndGet(value -> value <= 0 ? 0 : value - 1);
    }

    private static BlockPos findWaterPosition(ServerWorld world, ChunkPos chunkPos) {
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        for (int attempt = 0; attempt < 12; attempt++) {
            int x = startX + world.random.nextInt(16);
            int z = startZ + world.random.nextInt(16);
            int y = world.getTopY() - 1;
            while (y > world.getBottomY()) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = world.getBlockState(pos);
                if (state.isOf(Blocks.WATER)) {
                    if (world.getBlockState(pos.up()).isAir()) {
                        return pos;
                    }
                    break;
                }
                if (!state.isAir()) {
                    break;
                }
                y--;
            }
        }
        return null;
    }

    private static float calculateWaterDepth(ServerWorld world, BlockPos pos) {
        float depth = 0.0F;
        BlockPos check = pos.down();
        while (check.getY() > world.getBottomY()) {
            if (world.getBlockState(check).isOf(Blocks.WATER)) {
                depth += 1.0F;
                check = check.down();
            } else {
                break;
            }
        }
        return depth;
    }

    private static FishSpecies selectSpecies(ServerWorld world, float depth) {
        List<FishSpecies> candidates = new ArrayList<>();
        for (FishSpecies species : FishSpecies.getAll()) {
            if (FishingMechanics.matchesDepth(species.depth(), depth)) {
                candidates.add(species);
            }
        }
        if (candidates.isEmpty()) {
            candidates = FishSpecies.getAll();
        }
        if (candidates.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (FishSpecies species : candidates) {
            totalWeight += species.spawnWeight();
        }
        if (totalWeight <= 0) {
            return candidates.get(0);
        }
        int roll = world.random.nextInt(totalWeight);
        for (FishSpecies species : candidates) {
            roll -= species.spawnWeight();
            if (roll < 0) {
                return species;
            }
        }
        return candidates.get(0);
    }

    public static void cleanup() {
        ACTIVE_FISH.set(0);
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static int getActiveFishCount() {
        return ACTIVE_FISH.get();
    }
}
