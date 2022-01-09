package com.buuz135.findme.fabric;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.network.PositionRequestMessage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class FindMeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FindMeMod.init();
        FindMeMod.BLOCK_CHECKERS.add((blockEntity, stack) -> {
            try (Transaction transaction = Transaction.openOuter()) {
                for (Direction value : Direction.values()) {
                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, value);
                    if (storage == null) continue;
                    for (StorageView<ItemVariant> itemVariantStorageView : storage.iterable(transaction)) {
                        ItemStack invStack = itemVariantStorageView.getResource().toStack();
                        if (!invStack.isEmpty() && PositionRequestMessage.compareItems(stack, invStack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }
}
