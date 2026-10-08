package com.fishingplanet.fishingplanet.command;

import com.fishingplanet.fishingplanet.config.ModConfig;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.item.FishItem;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import com.fishingplanet.fishingplanet.registry.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FPCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
            net.minecraft.command.CommandManager.literal("fp")
                .requires(source -> source.hasPermissionLevel(2))
                .then(net.minecraft.command.CommandManager.literal("give")
                    .then(net.minecraft.command.CommandManager.argument("item", StringArgumentType.string())
                        .then(net.minecraft.command.CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
                            .executes(FPCommands::giveItem))
                        .executes(ctx -> giveItem(ctx, 1))))
                .then(net.minecraft.command.CommandManager.literal("spawn")
                    .then(net.minecraft.command.CommandManager.argument("species", StringArgumentType.string())
                        .then(net.minecraft.command.CommandManager.argument("count", IntegerArgumentType.integer(1, 100))
                            .then(net.minecraft.command.CommandManager.argument("radius", IntegerArgumentType.integer(1, 50))
                                .executes(FPCommands::spawnFish)))
                        .executes(FPCommands::spawnFish)))
                .then(net.minecraft.command.CommandManager.literal("frequency")
                    .then(net.minecraft.command.CommandManager.argument("multiplier", FloatArgumentType.floatArg(0.1f, 10.0f))
                        .executes(FPCommands::setFrequency)))
                .then(net.minecraft.command.CommandManager.literal("density")
                    .then(net.minecraft.command.CommandManager.argument("fishPerChunk", IntegerArgumentType.integer(1, 200))
                        .executes(FPCommands::setDensity)))
                .then(net.minecraft.command.CommandManager.literal("list")
                    .executes(FPCommands::listSpecies))
                .then(net.minecraft.command.CommandManager.literal("reload")
                    .executes(FPCommands::reloadConfig))
        );
    }

    private static int giveItem(CommandContext<ServerCommandSource> ctx) {
        return giveItem(ctx, IntegerArgumentType.getInteger(ctx, "count"));
    }

    private static int giveItem(CommandContext<ServerCommandSource> ctx, int count) {
        ServerCommandSource source = ctx.getSource();
        String itemName = StringArgumentType.getString(ctx, "item").toLowerCase();
        
        ItemStack stack = null;
        
        switch (itemName) {
            case "rod":
            case "fishing_rod":
            case "rod_item":
                stack = new ItemStack(ModItems.ROD_ITEM, count);
                break;
            case "reel":
            case "fishing_reel":
            case "reel_item":
                stack = new ItemStack(ModItems.REEL_ITEM, count);
                break;
            case "line":
            case "fishing_line":
            case "line_item":
                stack = new ItemStack(ModItems.LINE_ITEM, count);
                break;
            case "hook":
            case "fishing_hook":
            case "hook_item":
                stack = new ItemStack(ModItems.HOOK_ITEM, count);
                break;
            case "lure":
            case "fishing_lure":
            case "lure_item":
                stack = new ItemStack(ModItems.LURE_ITEM, count);
                break;
            case "bobber":
            case "float":
            case "bobber_item":
                stack = new ItemStack(ModItems.BOBBER_ITEM, count);
                break;
            case "tackle_box":
            case "tacklebox":
                stack = new ItemStack(ModItems.TACKLE_BOX, count);
                break;
            default:
                // Try to find fish species
                FishSpecies species = findFishSpecies(itemName);
                if (species != null) {
                    stack = new ItemStack(ModItems.FISH_ITEM, count);
                    FishItem fishItem = (FishItem) stack.getItem();
                    fishItem.setSpeciesId(stack, species.id());
                } else {
                    source.sendError(Text.literal("Unknown item: " + itemName).formatted(Formatting.RED));
                    return 0;
                }
        }
        
        if (stack != null) {
            source.getPlayerOrThrow().giveItemStack(stack);
            source.sendFeedback(() -> Text.literal("Gave " + count + "x " + itemName).formatted(Formatting.GREEN), true);
            return count;
        }
        
        return 0;
    }

    private static FishSpecies findFishSpecies(String name) {
        name = name.toLowerCase().replace(" ", "_").replace("-", "_");
        for (FishSpecies species : FishSpecies.getAll()) {
            if (species.name().toLowerCase().equals(name) || 
                species.displayName().toLowerCase().replace(" ", "_").equals(name)) {
                return species;
            }
        }
        return null;
    }

    private static int spawnFish(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();
        String speciesName = ctx.getArgument("species", String.class);
        int count = ctx.getArgument("count", Integer.class, 1);
        int radius = ctx.getArgument("radius", Integer.class, 10);
        
        FishSpecies species = findFishSpecies(speciesName);
        if (species == null) {
            source.sendError(Text.literal("Unknown species: " + speciesName).formatted(Formatting.RED));
            return 0;
        }
        
        BlockPos center = source.getPosition().toBlockPos();
        int spawned = 0;
        
        for (int i = 0; i < count; i++) {
            // Find water near player
            BlockPos spawnPos = center.add(
                world.random.nextBetween(-radius, radius),
                0,
                world.random.nextBetween(-radius, radius)
            );
            
            // Adjust Y to water surface
            while (spawnPos.getY() > world.getBottomY() && !world.getBlockState(spawnPos).isOf(net.minecraft.block.Blocks.WATER)) {
                spawnPos = spawnPos.down();
            }
            
            if (world.getBlockState(spawnPos).isOf(net.minecraft.block.Blocks.WATER)) {
                FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
                fish.setSpecies(species);
                fish.refreshPositionAndAngles(
                    spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5,
                    world.random.nextFloat() * 360, 0
                );
                world.spawnEntity(fish);
                spawned++;
            }
        }
        
        source.sendFeedback(() -> Text.literal("Spawned " + spawned + "x " + species.displayName()).formatted(Formatting.GREEN), true);
        return spawned;
    }

    private static int setFrequency(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        float multiplier = FloatArgumentType.getFloat(ctx, "multiplier");
        
        ModConfig config = ModConfig.get();
        config.spawn.spawnRateMultiplier = multiplier;
        ModConfig.save();
        
        source.sendFeedback(() -> Text.literal("Set fish spawn frequency to " + multiplier + "x").formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int setDensity(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        int density = IntegerArgumentType.getInteger(ctx, "fishPerChunk");
        
        ModConfig config = ModConfig.get();
        config.spawn.maxFishPerChunk = density;
        ModConfig.save();
        
        source.sendFeedback(() -> Text.literal("Set max fish per chunk to " + density).formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int listSpecies(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Fishing Planet Fish Species ===").formatted(Formatting.GOLD), false);
        
        String currentCategory = "";
        for (FishSpecies species : FishSpecies.getAll()) {
            if (!species.category().equals(currentCategory)) {
                currentCategory = species.category();
                source.sendFeedback(() -> Text.literal("\n[" + currentCategory + "]").formatted(Formatting.AQUA), false);
            }
            source.sendFeedback(() -> Text.literal("  " + species.displayName() + " (ID: " + species.id() + ") - " + species.rarity()).formatted(Formatting.GRAY), false);
        }
        
        source.sendFeedback(() -> Text.literal("\nTotal: " + FishSpecies.getAll().size() + " species").formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ModConfig.load();
        source.sendFeedback(() -> Text.literal("Config reloaded!").formatted(Formatting.GREEN), true);
        return 1;
    }
}