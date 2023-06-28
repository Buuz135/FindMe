package com.buuz135.findme.tracking;

import net.minecraft.world.item.ItemStack;

public class TrackingList {
    private static ItemStack stackB = ItemStack.EMPTY;
    private static ItemStack toTrack = ItemStack.EMPTY;

    public static boolean beingTracked(ItemStack stackA) {
        return ItemStack.isSameItemSameTags(stackA, stackB);
    }

    public static void clear() {
        stackB = ItemStack.EMPTY;
        toTrack = ItemStack.EMPTY;
    }

    public static void trackItem(ItemStack stack) {
        toTrack = stack.copy();
    }

    public static void beginTracking() {
        stackB = toTrack;
    }
}
