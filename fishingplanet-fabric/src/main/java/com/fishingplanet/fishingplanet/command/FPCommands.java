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
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class FPCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("fp")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("give")
                .then(CommandManager.argument("item", StringArgumentType.string())
                    .executes(FPCommands::giveDefault)
                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
                        .executes(FPCommands::giveCounted))))
            .then(CommandManager.literal("spawn")
                .then(CommandManager.argument("species", StringArgumentType.string())
                    .executes(FPCommands::spawnDefault)
                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 100))
                        .executes(FPCommands::spawnCounted)
                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 50))
                            .executes(FPCommands::spawnFull)))))
            .then(CommandManager.literal("frequency")
                .then(CommandManager.argument("multiplier", FloatArgumentType.floatArg(0.1F, 10.0F))
                    .executes(FPCommands::setFrequency)))
            .then(CommandManager.literal("density")
                .then(CommandManager.argument("fishPerChunk", IntegerArgumentType.integer(1, 200))
                    .executes(FPCommands::setDensity)))
            .then(CommandManager.literal("list")
                .executes(FPCommands::listSpecies))
            .then(CommandManager.literal("reload")
                .executes(FPCommands::reloadConfig)));
    }

    private static int giveDefault(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return giveItem(ctx, 1);
    }

    private static int giveCounted(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return giveItem(ctx, IntegerArgumentType.getInteger(ctx, "count"));
    }

    private static int giveItem(CommandContext<ServerCommandSource> ctx, int count) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        String itemName = StringArgumentType.getString(ctx, "item").toLowerCase();
        ItemStack stack;
        switch (itemName) {
            case "rod":
            case "fishing_rod":
                stack = new ItemStack(ModItems.ROD_ITEM, count);
                break;
            case "reel":
            case "fishing_reel":
                stack = new ItemStack(ModItems.REEL_ITEM, count);
                break;
            case "line":
            case "fishing_line":
                stack = new ItemStack(ModItems.LINE_ITEM, count);
                break;
            case "hook":
            case "fishing_hook":
                stack = new ItemStack(ModItems.HOOK_ITEM, count);
                break;
            case "lure":
            case "fishing_lure":
                stack = new ItemStack(ModItems.LURE_ITEM, count);
                break;
            case "bobber":
            case "float":
                stack = new ItemStack(ModItems.BOBBER_ITEM, count);
                break;
            case "tackle_box":
            case "tacklebox":
                stack = new ItemStack(ModItems.TACKLE_BOX, count);
                break;
            default:
                FishSpecies species = findFishSpecies(itemName);
                if (species == null) {
                    source.sendError(Text.literal("Unknown item: " + itemName).formatted(Formatting.RED));
                    return 0;
                }
                stack = new ItemStack(ModItems.FISH_ITEM, count);
                ((FishItem) stack.getItem()).setSpeciesId(stack, species.id());
                break;
        }
        source.getPlayerOrThrow().giveItemStack(stack);
        int result = count;
        source.sendFeedback(() -> Text.literal("Gave " + result + "x " + itemName).formatted(Formatting.GREEN), true);
        return result;
    }

    private static FishSpecies findFishSpecies(String name) {
        String key = name.toLowerCase().replace(" ", "_").replace("-", "_");
        for (FishSpecies species : FishSpecies.getAll()) {
            if (species.name().toLowerCase().equals(key)
                || species.displayName().toLowerCase().replace(" ", "_").equals(key)) {
                return species;
            }
        }
        return null;
    }

    private static int spawnDefault(CommandContext<ServerCommandSource> ctx) {
        return spawnFish(ctx, 1, 10);
    }

    private static int spawnCounted(CommandContext<ServerCommandSource> ctx) {
        return spawnFish(ctx, IntegerArgumentType.getInteger(ctx, "count"), 10);
    }

    private static int spawnFull(CommandContext<ServerCommandSource> ctx) {
        return spawnFish(ctx, IntegerArgumentType.getInteger(ctx, "count"), IntegerArgumentType.getInteger(ctx, "radius"));
    }

    private static int spawnFish(CommandContext<ServerCommandSource> ctx, int count, int radius) {
        ServerCommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();
        String speciesName = StringArgumentType.getString(ctx, "species");
        FishSpecies species = findFishSpecies(speciesName);
        if (species == null) {
            source.sendError(Text.literal("Unknown species: " + speciesName).formatted(Formatting.RED));
            return 0;
        }
        BlockPos center = BlockPos.ofFloored(source.getPosition());
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            int dx = world.random.nextInt(radius * 2 + 1) - radius;
            int dz = world.random.nextInt(radius * 2 + 1) - radius;
            BlockPos spawnPos = findWaterNear(world, center.add(dx, 0, dz));
            if (spawnPos == null) {
                continue;
            }
            FishingPlanetFishEntity fish = new FishingPlanetFishEntity(ModEntities.FISHING_PLANET_FISH, world);
            fish.setSpecies(species);
            fish.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                world.random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(fish);
            spawned++;
        }
        int result = spawned;
        source.sendFeedback(() -> Text.literal("Spawned " + result + "x " + species.displayName()).formatted(Formatting.GREEN), true);
        return result;
    }

    private static BlockPos findWaterNear(ServerWorld world, BlockPos pos) {
        BlockPos check = new BlockPos(pos.getX(), Math.min(pos.getY() + 5, world.getTopY() - 1), pos.getZ());
        for (int i = 0; i < 25 && check.getY() > world.getBottomY(); i++) {
            if (world.getBlockState(check).isOf(Blocks.WATER)) {
                return check;
            }
            check = check.down();
        }
        return null;
    }

    private static int setFrequency(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        float multiplier = FloatArgumentType.getFloat(ctx, "multiplier");
        ModConfig.get().spawn.spawnRateMultiplier = multiplier;
        ModConfig.save();
        source.sendFeedback(() -> Text.literal("Set fish spawn frequency to " + multiplier + "x").formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int setDensity(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        int density = IntegerArgumentType.getInteger(ctx, "fishPerChunk");
        ModConfig.get().spawn.maxFishPerChunk = density;
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
                String header = "\n[" + currentCategory + "]";
                source.sendFeedback(() -> Text.literal(header).formatted(Formatting.AQUA), false);
            }
            String line = "  " + species.displayName() + " (ID: " + species.id() + ") - " + species.rarity();
            source.sendFeedback(() -> Text.literal(line).formatted(Formatting.GRAY), false);
        }
        source.sendFeedback(() -> Text.literal("Total: " + FishSpecies.getAll().size() + " species").formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ModConfig.load();
        source.sendFeedback(() -> Text.literal("Config reloaded!").formatted(Formatting.GREEN), true);
        return 1;
    }
}
