package com.buuz135.findme.network;


import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.client.ClientTickHandler;
import com.buuz135.findme.client.ParticlePosition;
import com.buuz135.findme.tracking.TrackingList;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class PositionResponseMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<PositionResponseMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FindMeMod.MOD_ID, "position_response"));
    public static StreamCodec<? super RegistryFriendlyByteBuf, PositionResponseMessage> CODEC = new StreamCodec<>() {
        @Override
        public PositionResponseMessage decode(RegistryFriendlyByteBuf object) {
            List<BlockPos> positions = new ArrayList<>();
            int amount = object.readInt();
            while (amount > 0) {
                positions.add(object.readBlockPos());
                --amount;
            }
            return new PositionResponseMessage(positions);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, PositionResponseMessage positionRequestMessage) {
            registryFriendlyByteBuf.writeInt(positionRequestMessage.positions.size());
            for (BlockPos position : positionRequestMessage.positions) {
                registryFriendlyByteBuf.writeBlockPos(position);
            }
        }
    };

    private List<BlockPos> positions;

    public PositionResponseMessage(List<BlockPos> positions) {
        this.positions = positions;
    }

    public PositionResponseMessage() {
    }


    public void handle(NetworkManager.PacketContext context) {
        Minecraft.getInstance().execute(() -> {
            if (positions.size() > 0) {
                Minecraft.getInstance().player.closeContainer();
                Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                if (FindMeMod.CONFIG.CLIENT.CONTAINER_TRACKING) {
                    TrackingList.beginTracking();
                    ClientTickHandler.addRunnable(TrackingList::clear, FindMeMod.CONFIG.CLIENT.CONTAINER_TRACK_TIME);
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
        Minecraft.getInstance().particleEngine.add(new ParticlePosition(Minecraft.getInstance().level, position.getX() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, position.getY() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, 0, 0, 0));
        //Minecraft.getInstance().particleEngine.add(new AshParticle((ClientLevel) Minecraft.getInstance().player.level(), position.getX() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, 1 + position.getY() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, position.getZ() + 0.75 - Minecraft.getInstance().player.level().random.nextDouble() / 2D, 0, 0, 0));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
