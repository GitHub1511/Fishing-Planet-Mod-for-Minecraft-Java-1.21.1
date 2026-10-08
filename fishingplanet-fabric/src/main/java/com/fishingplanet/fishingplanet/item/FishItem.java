package com.fishingplanet.fishingplanet.item;

import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class FishItem extends Item {
    private int speciesId = 61; // Default Pumpkinseed

    public FishItem(Settings settings) {
        super(settings.maxCount(1));
    }

    public static FishItem ofSpecies(int speciesId) {
        FishItem item = new FishItem(new Settings().maxCount(1));
        item.speciesId = speciesId;
        return item;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        FishSpecies species = FishSpecies.getById(this.getSpeciesId(stack));
        tooltip.add(Text.literal(species.displayName).formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Category: " + species.category).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Habitat: " + species.habitat).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Depth: " + species.depth).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Rarity: " + species.rarity).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Weight: " + species.minWeightKg + "-" + species.maxWeightKg + "kg").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Length: " + species.minLengthCm + "-" + species.maxLengthCm + "cm").formatted(Formatting.GRAY));
        
        // Show actual caught fish stats if present
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("CaughtWeight")) {
            float weight = nbt.getFloat("CaughtWeight");
            float length = nbt.getFloat("CaughtLength");
            int form = nbt.getInt("CaughtForm");
            String[] forms = {"Young", "Common", "Trophy", "Unique"};
            tooltip.add(Text.literal("--- Caught Specimen ---").formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Weight: " + String.format("%.2f", weight) + "kg").formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Length: " + String.format("%.1f", length) + "cm").formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Form: " + forms[Math.min(form, 3)]).formatted(Formatting.AQUA));
        }
    }

    public int getSpeciesId(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("SpeciesId")) {
            return nbt.getInt("SpeciesId");
        }
        return this.speciesId;
    }

    public void setSpeciesId(ItemStack stack, int speciesId) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("SpeciesId", speciesId);
    }

    public void setCaughtData(ItemStack stack, float weight, float length, int form) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putFloat("CaughtWeight", weight);
        nbt.putFloat("CaughtLength", length);
        nbt.putInt("CaughtForm", form);
    }
}