package com.fishingplanet.fishingplanet.fishing;

import com.fishingplanet.fishingplanet.entity.bobber.FishingPlanetBobberEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.item.*;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import com.fishingplanet.fishingplanet.registry.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class FishingMechanics {
    public static void register() {
        // Hook into vanilla fishing
    }

    public static FishingPlanetBobberEntity createBobber(PlayerEntity player, ItemStack rodStack) {
        World world = player.getWorld();
        FishingPlanetBobberEntity bobber = new FishingPlanetBobberEntity(
            ModEntities.FISHING_PLANET_BOBBER, world, player, rodStack);
        
        // Apply tackle from rod
        if (rodStack.getItem() instanceof RodItem rod) {
            bobber.setTackleType(rod.getTackleType());
        }
        
        return bobber;
    }

    public static void onFishHooked(FishingPlanetBobberEntity bobber, LivingEntity fish) {
        World world = bobber.getWorld();
        if (world.isClient()) return;
        
        // Play hook sound
        world.playSound(null, bobber.getBlockPos(), ModSounds.FISH_HOOKED, SoundCategory.PLAYERS, 1.0f, 1.0f);
        
        // Spawn particles
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH, 
                bobber.getX(), bobber.getY(), bobber.getZ(), 
                10, 0.5, 0.2, 0.5, 0.1);
        }
        
        bobber.setHasBite(true);
    }

    public static void onFishCaught(ServerWorld world, PlayerEntity player, FishingPlanetFishEntity fish, ItemStack rodStack) {
        // Generate caught fish item with actual stats
        ItemStack fishStack = new ItemStack(ModItems.FISH_ITEM);
        FishItem fishItem = (FishItem) fishStack.getItem();
        fishItem.setSpeciesId(fishStack, fish.getSpecies().id());
        fishItem.setCaughtData(fishStack, fish.getFishWeight(), fish.getFishLength(), fish.getFishForm());
        
        // Add custom name with weight
        fishStack.setCustomName(
            net.minecraft.text.Text.literal(
                fish.getSpecies().displayName + " (" + 
                String.format("%.2f", fish.getFishWeight()) + "kg, " +
                String.format("%.1f", fish.getFishLength()) + "cm)"
            ).formatted(net.minecraft.util.Formatting.GOLD)
        );
        
        // Give to player or drop
        if (!player.getInventory().insertStack(fishStack)) {
            player.dropItem(fishStack, false);
        }
        
        // Play catch sound
        world.playSound(null, player.getBlockPos(), ModSounds.FISH_BITE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        
        // Award experience
        player.addExperience(Math.max(1, (int)(fish.getFishWeight() * 2)));
    }

    public static void onFishEscape(FishingPlanetBobberEntity bobber) {
        World world = bobber.getWorld();
        world.playSound(null, bobber.getBlockPos(), ModSounds.FISH_ESCAPE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        bobber.setHasBite(false);
    }

    public static float calculateCatchChance(PlayerEntity player, ItemStack rodStack, Biome biome, float depth) {
        float chance = 0.1f; // Base 10%
        
        // Rod bonuses
        if (rodStack.getItem() instanceof RodItem rod) {
            chance += rod.getTier() * 0.02f;
            chance += rod.getPower() * 0.05f;
        }
        
        // Biome modifiers
        if (biome.getCategory() == Biome.Category.OCEAN) chance *= 1.2f;
        else if (biome.getCategory() == Biome.Category.RIVER) chance *= 1.1f;
        else if (biome.getCategory() == Biome.Category.SWAMP) chance *= 1.3f;
        
        // Depth modifiers
        if (depth > 5) chance *= 1.1f;
        if (depth > 15) chance *= 1.2f;
        
        // Time of day
        long time = world.getTimeOfDay() % 24000;
        if (time < 1000 || time > 23000) chance *= 1.3f; // Night
        else if (time > 5000 && time < 7000) chance *= 1.2f; // Dawn
        else if (time > 11000 && time < 13000) chance *= 1.1f; // Dusk
        
        // Weather
        if (world.isRaining()) chance *= 1.5f;
        if (world.isThundering()) chance *= 0.5f;
        
        return Math.min(chance, 0.9f); // Cap at 90%
    }

    public static FishSpecies selectFishSpecies(ServerWorld world, Biome biome, float depth, ItemStack lureStack) {
        // Get valid species for this biome/depth
        java.util.List<FishSpecies> validSpecies = FishSpecies.getByHabitat(biome.getCategory().name());
        
        // Filter by depth preference
        validSpecies = validSpecies.stream()
            .filter(s -> matchesDepth(s.depth, depth))
            .toList();
        
        // Filter by lure preference
        if (lureStack.getItem() instanceof LureItem lure) {
            validSpecies = validSpecies.stream()
                .filter(s -> s.category.contains(lure.category) || lure.target.contains(s.name))
                .toList();
        }
        
        if (validSpecies.isEmpty()) {
            validSpecies = FishSpecies.getAll();
        }
        
        // Weighted random selection
        int totalWeight = validSpecies.stream().mapToInt(s -> s.spawnWeight).sum();
        int roll = world.random.nextInt(totalWeight);
        
        for (FishSpecies species : validSpecies) {
            roll -= species.spawnWeight;
            if (roll < 0) return species;
        }
        
        return validSpecies.get(0);
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

    public static void spawnCaughtFish(ServerWorld world, PlayerEntity player, FishSpecies species, BlockPos pos) {
        FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
        fish.setSpecies(species);
        fish.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
        fish.setFromFishing(true);
        world.spawnEntity(fish);
    }
}