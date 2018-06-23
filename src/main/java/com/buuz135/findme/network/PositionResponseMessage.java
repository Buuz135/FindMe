package com.buuz135.findme.network;

import com.buuz135.findme.proxy.client.ParticlePosition;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PositionResponseMessage implements IMessage {

    private List<BlockPos> positions;

    public PositionResponseMessage(List<BlockPos> positions) {
        this.positions = positions;
    }

    public PositionResponseMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        int amount = packetBuffer.readInt();
        positions = new ArrayList<>();
        while (amount > 0) {
            positions.add(packetBuffer.readBlockPos());
            --amount;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeInt(positions.size());
        for (BlockPos position : positions) {
            packetBuffer.writeBlockPos(position);
        }
    }

    public static class Handler implements IMessageHandler<PositionResponseMessage, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PositionResponseMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().player.closeScreen();
                for (BlockPos position : message.positions) {
                    for (int i = 0; i < 2; ++i)
                        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticlePosition(Minecraft.getMinecraft().player.world, position.getX() + 0.75 - Minecraft.getMinecraft().player.world.rand.nextDouble() / 2D, position.getY() + 0.75 - Minecraft.getMinecraft().player.world.rand.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getMinecraft().player.world.rand.nextDouble() / 2D));
                }
            });
            return null;
        }
    }
}
