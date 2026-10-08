package com.fishingplanet.fishingplanet.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TackleBoxItem extends Item {
    public TackleBoxItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Tackle Box").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Holds rods, reels, lures and terminal tackle.").formatted(Formatting.GRAY));
    }
}
