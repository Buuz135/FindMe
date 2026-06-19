package com.buuz135.findme.network;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class PositionRequestMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<PositionRequestMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(FindMeMod.MOD_ID, "position_request"));
    public static StreamCodec<? super RegistryFriendlyByteBuf, PositionRequestMessage> CODEC = new StreamCodec<>() {
        @Override
        public PositionRequestMessage decode(RegistryFriendlyByteBuf object) {
            return new PositionRequestMessage(ItemStack.OPTIONAL_STREAM_CODEC.decode(object));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, PositionRequestMessage positionRequestMessage) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(registryFriendlyByteBuf, positionRequestMessage.stack);
        }
    };

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
                    blocks.add(new BlockPos((int) x, (int) y, (int) z));
                }
            }
        }
        return blocks;
    }

    public static boolean compareItems(ItemStack first, ItemStack second) {
        if (FindMeMod.CONFIG.COMMON.IGNORE_ITEM_DAMAGE)
            return ItemStack.isSameItem(first, second);
        return ItemStack.isSameItemSameComponents(first, second);
    }

    public void handle(NetworkManager.PacketContext contextSupplier) {
        contextSupplier.queue(() -> {
            AABB box = new AABB(contextSupplier.getPlayer().blockPosition()).inflate(FindMeMod.CONFIG.COMMON.RADIUS_RANGE);
            List<BlockPos> blockPosList = new ArrayList<>();
            for (BlockPos blockPos : getBlockPosInAABB(box)) {
                BlockEntity tileEntity = contextSupplier.getPlayer().level().getBlockEntity(blockPos);
                if (tileEntity != null && FindMeMod.BLOCK_CHECKERS.stream().anyMatch(predicate -> predicate.test(tileEntity, stack))) {
                    blockPosList.add(blockPos);
                }
            }
            if (!blockPosList.isEmpty()) {
                NetworkManager.sendToPlayer((ServerPlayer) contextSupplier.getPlayer(), new PositionResponseMessage(blockPosList));
            }


        });
        //contextSupplier.get().setPacketHandled(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
