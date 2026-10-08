package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class HookItem extends Item {
    private final String type;
    private final int size;
    private final String sizeStr;
    private final String wire;
    private final float gapMm;
    private final int price;

    public HookItem(Settings settings) {
        super(settings.maxCount(64));
        this.type = "Aberdeen";
        this.size = 4;
        this.sizeStr = "4";
        this.wire = "Medium";
        this.gapMm = 8.9f;
        this.price = 90;
    }

    public HookItem(Settings settings, String type, int size, String sizeStr, String wire, float gapMm, int price) {
        super(settings.maxCount(64));
        this.type = type;
        this.size = size;
        this.sizeStr = sizeStr;
        this.wire = wire;
        this.gapMm = gapMm;
        this.price = price;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(type + " Hook Size " + sizeStr).formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Wire: " + wire).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Gap: " + gapMm + "mm").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Price: $" + price).formatted(Formatting.GREEN));
    }
}