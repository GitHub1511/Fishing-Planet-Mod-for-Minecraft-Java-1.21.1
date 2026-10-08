package com.fishingplanet.fishingplanet.loot;

import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.fishing.FishingMechanics;
import com.fishingplanet.fishingplanet.item.FishItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SetFishCatchFunction extends ConditionalLootFunction {
    public static final MapCodec<SetFishCatchFunction> CODEC =
        MapCodec.unit(new SetFishCatchFunction(List.of()));

    public SetFishCatchFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType<SetFishCatchFunction> getType() {
        return ModLootFunctions.SET_FISH_CATCH;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof FishItem fishItem)) {
            return stack;
        }
        ServerWorld world = context.getWorld();
        float depth = 3.0F;
        Vec3d origin = context.get(LootContextParameters.ORIGIN);
        if (origin != null) {
            BlockPos pos = BlockPos.ofFloored(origin);
            int water = 0;
            for (int i = 0; i < 12; i++) {
                if (world.getBlockState(pos.down(i)).isOf(Blocks.WATER)) {
                    water++;
                } else {
                    break;
                }
            }
            depth = water;
        }
        FishSpecies species = FishingMechanics.selectFishSpecies(world, depth);
        float weight = (float) (species.minWeightKg()
            + world.random.nextFloat() * (species.maxWeightKg() - species.minWeightKg()));
        float length = (float) (species.minLengthCm()
            + world.random.nextFloat() * (species.maxLengthCm() - species.minLengthCm()));
        fishItem.setSpeciesId(stack, species.id());
        fishItem.setCaughtData(stack, weight, length, 1);
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(
            species.displayName() + " (" + String.format("%.2f", weight) + "kg, "
                + String.format("%.1f", length) + "cm)"
        ).formatted(Formatting.GOLD));
        return stack;
    }
}
