package com.fishingplanet.fishingplanet.fishing;

import com.fishingplanet.fishingplanet.config.ModConfig;
import com.fishingplanet.fishingplanet.entity.bobber.FishingPlanetBobberEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.item.FishItem;
import com.fishingplanet.fishingplanet.item.RodItem;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import com.fishingplanet.fishingplanet.registry.ModItems;
import com.fishingplanet.fishingplanet.registry.ModSounds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FishingMechanics {
    public static void register() {
    }

    public static FishingPlanetBobberEntity createBobber(PlayerEntity player, ItemStack rodStack) {
        World world = player.getWorld();
        return new FishingPlanetBobberEntity(ModEntities.FISHING_PLANET_BOBBER, world, player, rodStack);
    }

    public static void onFishHooked(FishingPlanetBobberEntity bobber, LivingEntity fish) {
        World world = bobber.getWorld();
        if (world.isClient()) {
            return;
        }
        world.playSound(null, bobber.getBlockPos(), ModSounds.FISH_HOOKED, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH, bobber.getX(), bobber.getY(), bobber.getZ(), 10, 0.5, 0.2, 0.5, 0.1);
        }
        bobber.setHasBite(true);
    }

    public static void onFishCaught(ServerWorld world, PlayerEntity player, FishingPlanetFishEntity fish) {
        FishSpecies species = fish.getSpecies();
        ItemStack fishStack = new ItemStack(ModItems.FISH_ITEM);
        FishItem fishItem = (FishItem) fishStack.getItem();
        fishItem.setSpeciesId(fishStack, species.id());
        fishItem.setCaughtData(fishStack, fish.getFishWeight(), fish.getFishLength(), fish.getFishForm());
        fishStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(
            species.displayName() + " (" + String.format("%.2f", fish.getFishWeight()) + "kg, "
                + String.format("%.1f", fish.getFishLength()) + "cm)"
        ).formatted(Formatting.GOLD));
        if (!player.getInventory().insertStack(fishStack)) {
            player.dropItem(fishStack, false);
        }
        world.playSound(null, player.getBlockPos(), ModSounds.FISH_BITE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        player.addExperience(Math.max(1, (int) (fish.getFishWeight() * 2)));
    }

    public static void onFishEscape(FishingPlanetBobberEntity bobber) {
        World world = bobber.getWorld();
        world.playSound(null, bobber.getBlockPos(), ModSounds.FISH_ESCAPE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        bobber.setHasBite(false);
    }

    public static float calculateCatchChance(ServerWorld world, PlayerEntity player, ItemStack rodStack, float depth) {
        float chance = 0.1F * ModConfig.get().fishing.catchChanceMultiplier;
        if (rodStack.getItem() instanceof RodItem rod) {
            chance += rod.getTier() * 0.02F;
            chance += rod.getPower() * 0.05F;
        }
        if (depth > 5.0F) {
            chance *= 1.1F;
        }
        if (depth > 15.0F) {
            chance *= 1.2F;
        }
        long time = world.getTimeOfDay() % 24000L;
        if (time < 1000L || time > 23000L) {
            chance *= 1.3F;
        } else if (time > 5000L && time < 7000L) {
            chance *= 1.2F;
        } else if (time > 11000L && time < 13000L) {
            chance *= 1.1F;
        }
        if (world.isRaining()) {
            chance *= 1.5F;
        }
        if (world.isThundering()) {
            chance *= 0.5F;
        }
        return Math.min(chance, 0.9F);
    }

    public static FishSpecies selectFishSpecies(ServerWorld world, float depth) {
        List<FishSpecies> valid = new ArrayList<>();
        for (FishSpecies species : FishSpecies.getAll()) {
            if (matchesDepth(species.depth(), depth)) {
                valid.add(species);
            }
        }
        if (valid.isEmpty()) {
            valid = FishSpecies.getAll();
        }
        int totalWeight = 0;
        for (FishSpecies species : valid) {
            totalWeight += species.spawnWeight();
        }
        if (totalWeight <= 0) {
            return valid.get(0);
        }
        int roll = world.random.nextInt(totalWeight);
        for (FishSpecies species : valid) {
            roll -= species.spawnWeight();
            if (roll < 0) {
                return species;
            }
        }
        return valid.get(0);
    }

    public static boolean matchesDepth(String speciesDepth, float depth) {
        if (speciesDepth == null) {
            return true;
        }
        return switch (speciesDepth.toLowerCase()) {
            case "shallow" -> depth < 3.0F;
            case "mid" -> depth >= 3.0F && depth < 10.0F;
            case "bottom" -> depth >= 10.0F;
            case "top" -> depth < 2.0F;
            case "pelagic" -> depth > 20.0F;
            case "shallow-mid" -> depth < 8.0F;
            case "mid-top" -> depth > 2.0F && depth < 15.0F;
            case "mid-bottom" -> depth > 5.0F;
            default -> true;
        };
    }

    public static void spawnCaughtFish(ServerWorld world, PlayerEntity player, FishSpecies species, BlockPos pos) {
        FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
        fish.setSpecies(species);
        fish.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0F, 0.0F);
        fish.setFromFishing(true);
        world.spawnEntity(fish);
    }
}
