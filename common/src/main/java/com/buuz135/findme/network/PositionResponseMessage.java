package com.buuz135.findme.network;


import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.FindMeModClient;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PositionResponseMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<PositionResponseMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(FindMeMod.MOD_ID, "position_response"));
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
        if (context.getEnvironment() != Env.CLIENT || Platform.getEnvironment() != Env.CLIENT) {
            return;
        }
        List<BlockPos> responsePositions = List.copyOf(positions);
        context.queue(() -> FindMeModClient.handlePositionResponse(responsePositions));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
