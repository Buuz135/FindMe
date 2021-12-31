package com.buuz135.findme.network;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PositionRequestMessage {

    private ItemStack stack;

    public PositionRequestMessage(ItemStack stack) {
        this.stack = stack;
        TrackingList.trackItem(stack);
    }

    public PositionRequestMessage() {
    }

    public static List<BlockPos> getBlockPosInAABB(AABB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
            for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
                for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public PositionRequestMessage fromBytes(ByteBuf buf) {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        stack = ItemStack.EMPTY;
        stack = packetBuffer.readItem();
        return this;
    }

    public void toBytes(ByteBuf buf) {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        packetBuffer.writeItem(stack);
    }

    public static boolean compareItems(ItemStack first, ItemStack second) {
        if (!FindMeMod.CONFIG.COMMON.IGNORE_ITEM_DAMAGE)
            return first.sameItemStackIgnoreDurability(second);
        return first.sameItem(second);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            AABB box = new AABB(contextSupplier.get().getPlayer().blockPosition()).inflate(FindMeMod.CONFIG.COMMON.RADIUS_RANGE);
            List<BlockPos> blockPosList = new ArrayList<>();
            for (BlockPos blockPos : getBlockPosInAABB(box)) {
                BlockEntity tileEntity = contextSupplier.get().getPlayer().level.getBlockEntity(blockPos);
                if (tileEntity != null && FindMeMod.BLOCK_CHECKERS.stream().anyMatch(predicate -> predicate.test(tileEntity, stack))) {
                    blockPosList.add(blockPos);
                }
            }
            if (!blockPosList.isEmpty())
                FindMeMod.CHANNEL.sendToPlayer((ServerPlayer) contextSupplier.get().getPlayer(),new PositionResponseMessage(blockPosList));

        });
        //contextSupplier.get().setPacketHandled(true);
    }

}
