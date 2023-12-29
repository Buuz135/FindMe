package com.buuz135.findme.network;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.IInventoryPuller;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.function.Supplier;

public class PullItemRequestMessage {

    private ItemStack stack;
    private int amount;

    public PullItemRequestMessage(ItemStack stack, int amount) {
        this.stack = stack;
        this.amount = amount;
        TrackingList.trackItem(stack);
    }

    public PullItemRequestMessage() {

    }

    public static boolean compareItems(ItemStack first, ItemStack second) {
        if (FindMeMod.CONFIG.COMMON.IGNORE_ITEM_DAMAGE)
            return ItemStack.isSameItem(first, second);
        return ItemStack.isSameItemSameTags(first, second);
    }

    public PullItemRequestMessage fromBytes(ByteBuf buf) {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        stack = ItemStack.EMPTY;
        stack = packetBuffer.readItem();
        amount = packetBuffer.readInt();
        return this;
    }

    public void toBytes(ByteBuf buf) {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        packetBuffer.writeItem(stack);
        packetBuffer.writeInt(amount);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            AABB box = new AABB(contextSupplier.get().getPlayer().blockPosition()).inflate(FindMeMod.CONFIG.COMMON.RADIUS_RANGE);
            var currentAmount = 0;
            for (BlockPos blockPos : PositionRequestMessage.getBlockPosInAABB(box)) {
                BlockEntity tileEntity = contextSupplier.get().getPlayer().level().getBlockEntity(blockPos);
                if (tileEntity != null) {
                    for (IInventoryPuller blockExtractor : FindMeMod.BLOCK_EXTRACTORS) {
                        currentAmount += blockExtractor.pull(tileEntity, stack, amount - currentAmount, contextSupplier.get().getPlayer());
                        if (currentAmount >= amount) {
                            break;
                        }
                    }
                }
                if (currentAmount >= amount) {
                    break;
                }
            }
            if (currentAmount < amount) {
                var player = contextSupplier.get().getPlayer();
                var level = player.level();
                level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        });
        //contextSupplier.get().setPacketHandled(true);
    }
}
