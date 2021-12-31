package com.buuz135.findme.network;


import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.client.ClientTickHandler;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;

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
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        int amount = packetBuffer.readInt();
        positions = new ArrayList<>();
        while (amount > 0) {
            positions.add(packetBuffer.readBlockPos());
            --amount;
        }
        return this;
    }

    public void toBytes(ByteBuf buf) {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(buf);
        packetBuffer.writeInt(positions.size());
        for (BlockPos position : positions) {
            packetBuffer.writeBlockPos(position);
        }
    }

    public void handle(Supplier<NetworkManager.PacketContext> context) {
        Minecraft.getInstance().execute(() -> {
            if (positions.size() > 0) {
                Minecraft.getInstance().player.closeContainer();
                if (FindMeMod.DO_TRACKING) {
                    TrackingList.beginTracking();
                    ClientTickHandler.addRunnable(TrackingList::clear, FindMeMod.TRACKING_TIME);
                }
                for (BlockPos position : positions) {
                    for (int i = 0; i < 2; ++i)
                        addParticle(position);
                }
            }
        });
        //context.get().setPacketHandled(true);
    }

    @Environment(EnvType.CLIENT)
    public void addParticle(BlockPos position) {
        Minecraft.getInstance().player.level.addParticle((ParticleOptions) FindMeMod.FINDME.get(), position.getX() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, position.getY() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, 0, 0, 0);
        //Minecraft.getInstance().particleEngine.add(new AshParticle((ClientLevel) Minecraft.getInstance().player.level, position.getX() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, position.getY() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getInstance().player.level.random.nextDouble() / 2D, 0, 0, 0));
    }

}
