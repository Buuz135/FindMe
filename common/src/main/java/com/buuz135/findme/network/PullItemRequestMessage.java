package com.buuz135.findme.network;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.IInventoryPuller;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

public class PullItemRequestMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<PullItemRequestMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FindMeMod.MOD_ID, "pull_item_request"));
    public static StreamCodec<? super RegistryFriendlyByteBuf, PullItemRequestMessage> CODEC = new StreamCodec<>() {
        @Override
        public PullItemRequestMessage decode(RegistryFriendlyByteBuf object) {
            return new PullItemRequestMessage(ItemStack.parseOptional(object.registryAccess(), Objects.requireNonNull(object.readNbt())), object.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, PullItemRequestMessage positionRequestMessage) {
            registryFriendlyByteBuf.writeNbt(positionRequestMessage.stack.saveOptional(registryFriendlyByteBuf.registryAccess()));
            registryFriendlyByteBuf.writeInt(positionRequestMessage.amount);
        }
    };

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
        return ItemStack.isSameItemSameComponents(first, second);
    }

    public void handle(NetworkManager.PacketContext contextSupplier) {
        contextSupplier.queue(() -> {
            AABB box = new AABB(contextSupplier.getPlayer().blockPosition()).inflate(FindMeMod.CONFIG.COMMON.RADIUS_RANGE);
            var currentAmount = 0;
            for (BlockPos blockPos : PositionRequestMessage.getBlockPosInAABB(box)) {
                BlockEntity tileEntity = contextSupplier.getPlayer().level().getBlockEntity(blockPos);
                if (tileEntity != null) {
                    for (IInventoryPuller blockExtractor : FindMeMod.BLOCK_EXTRACTORS) {
                        currentAmount += blockExtractor.pull(tileEntity, stack, amount - currentAmount, contextSupplier.getPlayer());
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
                var player = contextSupplier.getPlayer();
                var level = player.level();
                level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        });
        //contextSupplier.get().setPacketHandled(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
