package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class RodItem extends FishingRodItem {
    private final int tackleType;
    private final String rodType;
    private final int tier;
    private final float action;
    private final float power;
    private final int lengthCm;
    private final int castingWeightMin;
    private final int castingWeightMax;
    private final int lineRatingMin;
    private final int lineRatingMax;
    private final int price;
    private final int unlockLevel;

    public RodItem(Settings settings) {
        super(settings.maxCount(1));
        // Default values - will be overridden by data-driven system
        this.tackleType = 0;
        this.rodType = "Lure";
        this.tier = 1;
        this.action = 1.0f;
        this.power = 1.0f;
        this.lengthCm = 210;
        this.castingWeightMin = 10;
        this.castingWeightMax = 30;
        this.lineRatingMin = 6;
        this.lineRatingMax = 12;
        this.price = 5000;
        this.unlockLevel = 5;
    }

    public RodItem(Settings settings, int tackleType, String rodType, int tier, float action, float power,
                   int lengthCm, int castingWeightMin, int castingWeightMax, int lineRatingMin, int lineRatingMax,
                   int price, int unlockLevel) {
        super(settings.maxCount(1));
        this.tackleType = tackleType;
        this.rodType = rodType;
        this.tier = tier;
        this.action = action;
        this.power = power;
        this.lengthCm = lengthCm;
        this.castingWeightMin = castingWeightMin;
        this.castingWeightMax = castingWeightMax;
        this.lineRatingMin = lineRatingMin;
        this.lineRatingMax = lineRatingMax;
        this.price = price;
        this.unlockLevel = unlockLevel;
    }

    public int getTackleType() { return tackleType; }
    public String getRodType() { return rodType; }
    public int getTier() { return tier; }
    public float getAction() { return action; }
    public float getPower() { return power; }
    public int getLengthCm() { return lengthCm; }
    public int getCastingWeightMin() { return castingWeightMin; }
    public int getCastingWeightMax() { return castingWeightMax; }
    public int getLineRatingMin() { return lineRatingMin; }
    public int getLineRatingMax() { return lineRatingMax; }
    public int getPrice() { return price; }
    public int getUnlockLevel() { return unlockLevel; }

    public float getSinkRate() {
        return switch (tackleType) {
            case 1 -> 0.02f; // Float - slow sink
            case 3 -> 0.08f; // Feeder - medium sink
            case 4 -> 0.05f; // Bottom - medium-fast sink
            case 5 -> 0.1f;  // Spod - fast sink
            default -> 0.05f;
        };
    }

    public float getPullForce() {
        return 0.5f + (tier * 0.1f) + (power * 0.2f);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(this.rodType + " Rod").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Tier: " + tier).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Power: " + (int)(power * 10) + "/10").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Action: " + (int)(action * 10) + "/10").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Length: " + lengthCm + "cm").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Cast Weight: " + castingWeightMin + "-" + castingWeightMax + "g").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Line Rating: " + lineRatingMin + "-" + lineRatingMax + "lb").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Unlock Level: " + unlockLevel).formatted(Formatting.BLUE));
        
        // Show attached tackle
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("Reel")) {
            tooltip.add(Text.literal("Reel: Attached").formatted(Formatting.AQUA));
        }
        if (nbt.contains("Line")) {
            tooltip.add(Text.literal("Line: Attached").formatted(Formatting.AQUA));
        }
        if (nbt.contains("Lure")) {
            tooltip.add(Text.literal("Lure: Attached").formatted(Formatting.AQUA));
        }
        if (nbt.contains("Hook")) {
            tooltip.add(Text.literal("Hook: Attached").formatted(Formatting.AQUA));
        }
    }

    public static RodItem fromNbt(NbtCompound nbt) {
        return new RodItem(new Settings(),
            nbt.getInt("TackleType"),
            nbt.getString("RodType"),
            nbt.getInt("Tier"),
            nbt.getFloat("Action"),
            nbt.getFloat("Power"),
            nbt.getInt("LengthCm"),
            nbt.getInt("CastingWeightMin"),
            nbt.getInt("CastingWeightMax"),
            nbt.getInt("LineRatingMin"),
            nbt.getInt("LineRatingMax"),
            nbt.getInt("Price"),
            nbt.getInt("UnlockLevel")
        );
    }

    public void saveToNbt(NbtCompound nbt) {
        nbt.putInt("TackleType", tackleType);
        nbt.putString("RodType", rodType);
        nbt.putInt("Tier", tier);
        nbt.putFloat("Action", action);
        nbt.putFloat("Power", power);
        nbt.putInt("LengthCm", lengthCm);
        nbt.putInt("CastingWeightMin", castingWeightMin);
        nbt.putInt("CastingWeightMax", castingWeightMax);
        nbt.putInt("LineRatingMin", lineRatingMin);
        nbt.putInt("LineRatingMax", lineRatingMax);
        nbt.putInt("Price", price);
        nbt.putInt("UnlockLevel", unlockLevel);
    }
}