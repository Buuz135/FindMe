package com.buuz135.findme.network;

import com.buuz135.findme.FindMe;
import com.buuz135.findme.proxy.FindMeConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PositionRequestMessage {

    private ItemStack stack;

    public PositionRequestMessage(ItemStack stack) {
        this.stack = stack;
    }

    public PositionRequestMessage() {
    }

    public static List<BlockPos> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
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
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        stack = ItemStack.EMPTY;
        stack = packetBuffer.readItemStack();
        return this;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeItemStack(stack);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            AxisAlignedBB box = new AxisAlignedBB(contextSupplier.get().getSender().getPosition()).grow(FindMeConfig.COMMON.RADIUS_RANGE.get());
            List<BlockPos> blockPosList = new ArrayList<>();
            for (BlockPos blockPos : getBlockPosInAABB(box)) {
                TileEntity tileEntity = contextSupplier.get().getSender().world.getTileEntity(blockPos);
                if (tileEntity != null) {
                    tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(handler -> {
                        for (int i = 0; i < handler.getSlots(); i++) {
                            if (!handler.getStackInSlot(i).isEmpty() && compareItems(stack, handler.getStackInSlot(i))) {
                                blockPosList.add(blockPos);
                                break;
                            }
                        }
                    });
                    if (tileEntity instanceof IInventory) {
                        IInventory inventory = (IInventory) tileEntity;
                        if (inventory.isEmpty()) continue;
                        for (int i = 0; i < inventory.getSizeInventory(); i++) {
                            if (!inventory.getStackInSlot(i).isEmpty() && compareItems(stack, inventory.getStackInSlot(i))) {
                                blockPosList.add(blockPos);
                                break;
                            }
                        }
                    }

                }
            }
            if (!blockPosList.isEmpty())
                FindMe.NETWORK.sendTo(new PositionResponseMessage(blockPosList), contextSupplier.get().getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);

        });
    }

    private boolean compareItems(ItemStack first, ItemStack second) {
        if(FindMeConfig.COMMON.IGNORE_ITEM_DAMAGE.get())
            return first.isItemEqualIgnoreDurability(second);
        return first.isItemEqual(second);
    }

}
