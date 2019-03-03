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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PositionRequestMessage implements IMessage {

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

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        stack = ItemStack.EMPTY;
        try {
            stack = packetBuffer.readItemStack();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeItemStack(stack);
    }

    public static class Handler implements IMessageHandler<PositionRequestMessage, PositionResponseMessage> {

        @Override
        public PositionResponseMessage onMessage(PositionRequestMessage message, MessageContext ctx) {
            ctx.getServerHandler().player.world.getMinecraftServer().addScheduledTask(() -> {
                AxisAlignedBB box = new AxisAlignedBB(ctx.getServerHandler().player.getPosition()).grow(FindMeConfig.RADIUS_RANGE);
                List<BlockPos> blockPosList = new ArrayList<>();
                for (BlockPos blockPos : getBlockPosInAABB(box)) {
                    TileEntity tileEntity = ctx.getServerHandler().player.world.getTileEntity(blockPos);
                    if (tileEntity != null) {
                        if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                            IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                            if (handler != null) {
                                for (int i = 0; i < handler.getSlots(); i++) {
                                    if (!handler.getStackInSlot(i).isEmpty() && handler.getStackInSlot(i).isItemEqual(message.stack)) {
                                        blockPosList.add(blockPos);
                                        break;
                                    }
                                }
                            }
                        }
                        if (tileEntity instanceof IInventory) {
                            IInventory inventory = (IInventory) tileEntity;
                            if (inventory.isEmpty()) continue;
                            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                                if (!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).isItemEqual(message.stack)) {
                                    blockPosList.add(blockPos);
                                    break;
                                }
                            }
                        }

                    }
                }
                if (!blockPosList.isEmpty())
                    FindMe.NETWORK.sendTo(new PositionResponseMessage(blockPosList), ctx.getServerHandler().player);
            });
            return null;
        }
    }
}
