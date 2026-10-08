package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ReelItem extends Item {
    private final String reelType;
    private final int tier;
    private final float gearRatio;
    private final int bearings;
    private final int dragKg;
    private final int lineCapacity;
    private final int weightG;
    private final int price;
    private final int unlockLevel;

    public ReelItem(Settings settings) {
        super(settings.component(DataComponentTypes.MAX_STACK_SIZE, 1));
        this.reelType = "Spinning";
        this.tier = 1;
        this.gearRatio = 5.2f;
        this.bearings = 5;
        this.dragKg = 8;
        this.lineCapacity = 150;
        this.weightG = 250;
        this.price = 3000;
        this.unlockLevel = 5;
    }

    public ReelItem(Settings settings, String reelType, int tier, float gearRatio, int bearings,
                    int dragKg, int lineCapacity, int weightG, int price, int unlockLevel) {
        super(settings.component(DataComponentTypes.MAX_STACK_SIZE, 1));
        this.reelType = reelType;
        this.tier = tier;
        this.gearRatio = gearRatio;
        this.bearings = bearings;
        this.dragKg = dragKg;
        this.lineCapacity = lineCapacity;
        this.weightG = weightG;
        this.price = price;
        this.unlockLevel = unlockLevel;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(this.reelType + " Reel").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Tier: " + tier).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Gear Ratio: " + gearRatio + ":1").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Bearings: " + bearings).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Drag: " + dragKg + "kg").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Line Capacity: " + lineCapacity + "m").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Weight: " + weightG + "g").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
    }
}