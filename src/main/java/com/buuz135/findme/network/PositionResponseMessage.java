package com.buuz135.findme.network;

import com.buuz135.findme.proxy.client.ParticlePosition;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PositionResponseMessage implements Serializable {

    private List<BlockPos> positions;

    public PositionResponseMessage(List<BlockPos> positions) {
        this.positions = positions;
    }

    public PositionResponseMessage() {
    }

    public PositionResponseMessage fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        int amount = packetBuffer.readInt();
        positions = new ArrayList<>();
        while (amount > 0) {
            positions.add(packetBuffer.readBlockPos());
            --amount;
        }
        return this;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeInt(positions.size());
        for (BlockPos position : positions) {
            packetBuffer.writeBlockPos(position);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        Minecraft.getInstance().deferTask(() -> {
            if (positions.size() > 0) {
                Minecraft.getInstance().player.closeScreen();
                for (BlockPos position : positions) {
                    for (int i = 0; i < 2; ++i)
                        Minecraft.getInstance().particles.addEffect(new ParticlePosition(Minecraft.getInstance().player.world, position.getX() + 0.75 - Minecraft.getInstance().player.world.rand.nextDouble() / 2D, position.getY() + 0.75 - Minecraft.getInstance().player.world.rand.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getInstance().player.world.rand.nextDouble() / 2D, 0, 0, 0));
                }
            }
        });
    }

}
