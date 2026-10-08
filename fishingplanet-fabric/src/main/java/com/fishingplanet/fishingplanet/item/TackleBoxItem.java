package com.fishingplanet.fishingplanet.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class TackleBoxItem extends Item {
    public static final int SLOTS = 27; // 3x9 grid

    public TackleBoxItem(Settings settings) {
        super(settings.maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            int filled = 0;
            for (ItemStack itemStack : container.stream()) {
                if (!itemStack.isEmpty()) filled++;
            }
            tooltip.add(Text.literal("Tackle Box").formatted(Formatting.GOLD));
            tooltip.add(Text.literal("Slots: " + filled + "/" + SLOTS).formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Right-click to open").formatted(Formatting.BLUE));
        }
    }

    @Override
    public boolean onUseOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, playerEntity) -> new TackleBoxScreenHandler(syncId, inventory, context.getHand()),
                Text.literal("Tackle Box")
            ));
        }
        return true;
    }

    @Override
    public boolean onUse(ItemUsageContext context) {
        return onUseOnBlock(context);
    }
}