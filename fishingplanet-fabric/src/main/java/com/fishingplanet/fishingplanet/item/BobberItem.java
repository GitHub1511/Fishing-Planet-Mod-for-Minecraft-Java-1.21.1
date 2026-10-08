package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BobberItem extends Item {
    private final String bobberType;
    private final float buoyancy;
    private final int sensitivity;
    private final int price;

    public BobberItem(Settings settings) {
        super(settings.maxCount(16));
        this.bobberType = "Standard Float";
        this.buoyancy = 1.0f;
        this.sensitivity = 5;
        this.price = 100;
    }

    public BobberItem(Settings settings, String bobberType, float buoyancy, int sensitivity, int price) {
        super(settings.maxCount(16));
        this.bobberType = bobberType;
        this.buoyancy = buoyancy;
        this.sensitivity = sensitivity;
        this.price = price;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(bobberType).formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Buoyancy: " + (int)(buoyancy * 100) + "%").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sensitivity: " + sensitivity + "/10").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
    }
}