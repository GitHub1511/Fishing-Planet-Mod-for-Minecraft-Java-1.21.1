package com.fishingplanet.fishingplanet.spawn;

import com.fishingplanet.fishingplanet.config.ModConfig;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.fishing.FishingMechanics;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FishSpawner {
    private static final AtomicInteger ACTIVE_FISH = new AtomicInteger(0);
    private static final int TICK_INTERVAL = 200;
    private static final int ATTEMPTS_PER_PLAYER = 6;
    private static final int SPAWN_RADIUS = 48;
    private static boolean enabled = true;

    public static void register() {
    }

    public static void onServerTick(MinecraftServer server) {
        if (!enabled || !ModConfig.get().spawn.enableNaturalSpawning) {
            return;
        }
        if (server.getTicks() % TICK_INTERVAL != 0) {
            return;
        }
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (ACTIVE_FISH.get() >= ModConfig.get().spawn.maxFishTotal) {
                return;
            }
            ServerWorld world = player.getServerWorld();
            int max = (int) (ModConfig.get().spawn.maxFishPerChunk * ModConfig.get().spawn.spawnRateMultiplier);
            for (int i = 0; i < ATTEMPTS_PER_PLAYER; i++) {
                if (ACTIVE_FISH.get() >= ModConfig.get().spawn.maxFishTotal) {
                    return;
                }
                BlockPos pos = randomWaterSpot(world, player.getBlockPos());
                if (pos == null) {
                    continue;
                }
                if (countFishInChunk(world, new ChunkPos(pos)) >= max) {
                    continue;
                }
                float depth = calculateWaterDepth(world, pos);
                FishSpecies species = selectSpecies(world, depth);
                if (species == null) {
                    continue;
                }
                FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
                fish.setSpecies(species);
                fish.refreshPositionAndAngles(
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    world.random.nextFloat() * 360.0F, 0.0F);
                world.spawnEntity(fish);
                ACTIVE_FISH.incrementAndGet();
            }
        }
    }

    private static BlockPos randomWaterSpot(ServerWorld world, BlockPos center) {
        for (int attempt = 0; attempt < 4; attempt++) {
            int x = center.getX() + world.random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
            int z = center.getZ() + world.random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
            int y = center.getY() + world.random.nextInt(21) - 10;
            if (y < world.getBottomY() + 1 || y >= world.getTopY()) {
                continue;
            }
            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos).isOf(Blocks.WATER)
                && world.getBlockState(pos.up()).isAir()) {
                return pos;
            }
        }
        return null;
    }

    public static int countFishInChunk(ServerWorld world, ChunkPos chunkPos) {
        Box box = new Box(chunkPos.getStartX(), -64.0, chunkPos.getStartZ(),
            chunkPos.getStartX() + 16.0, 320.0, chunkPos.getStartZ() + 16.0);
        int count = 0;
        for (FishingPlanetFishEntity fish : world.getEntitiesByClass(
                FishingPlanetFishEntity.class, box, fish -> fish.isAlive())) {
            count++;
        }
        return count;
    }

    public static void onFishRemoved() {
        ACTIVE_FISH.updateAndGet(value -> value <= 0 ? 0 : value - 1);
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
