package com.buuz135.findme;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IInventoryPuller {

    int pull(BlockEntity entity, ItemStack stack, int amount, Player player);

}
