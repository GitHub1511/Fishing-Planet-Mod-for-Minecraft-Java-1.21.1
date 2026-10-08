package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LureItem extends Item {
    private final String category;
    private final String size;
    private final String color;
    private final String target;
    private final int price;
    private final int depthM;
    private final String action;

    public LureItem(Settings settings) {
        super(settings.maxCount(16));
        this.category = "Crankbait";
        this.size = "1/4 oz";
        this.color = "Natural";
        this.target = "Bass, Pike, Walleye";
        this.price = 300;
        this.depthM = 2;
        this.action = "Wobble";
    }

    public LureItem(Settings settings, String category, String size, String color, String target,
                    int price, int depthM, String action) {
        super(settings.maxCount(16));
        this.category = category;
        this.size = size;
        this.color = color;
        this.target = target;
        this.price = price;
        this.depthM = depthM;
        this.action = action;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(color + " " + category + " " + size).formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Category: " + category).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Size: " + size).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Action: " + action).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Depth: " + depthM + "m").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Target: " + target).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
    }
}