package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LineItem extends Item {
    private final String material;
    private final int testLb;
    private final float testKg;
    private final float diameterMm;
    private final String stretch;
    private final String visibility;
    private final String abrasion;
    private final int price;

    public LineItem(Settings settings) {
        super(settings.maxCount(64));
        this.material = "Mono";
        this.testLb = 10;
        this.testKg = 4.5f;
        this.diameterMm = 0.3f;
        this.stretch = "High";
        this.visibility = "Medium";
        this.abrasion = "Medium";
        this.price = 500;
    }

    public LineItem(Settings settings, String material, int testLb, float testKg, float diameterMm,
                    String stretch, String visibility, String abrasion, int price) {
        super(settings.maxCount(64));
        this.material = material;
        this.testLb = testLb;
        this.testKg = testKg;
        this.diameterMm = diameterMm;
        this.stretch = stretch;
        this.visibility = visibility;
        this.abrasion = abrasion;
        this.price = price;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(testLb + "lb " + material + " Line").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Test: " + testLb + "lb (" + testKg + "kg)").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Diameter: " + diameterMm + "mm").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Stretch: " + stretch).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Visibility: " + visibility).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Abrasion: " + abrasion).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
    }
}