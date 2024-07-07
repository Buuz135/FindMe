package com.buuz135.findme.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class CustomParticleType extends ParticleType<CustomParticleType> implements ParticleOptions {

    private final StreamCodec<RegistryFriendlyByteBuf, CustomParticleType> streamCodec = StreamCodec.unit(this);
    private final MapCodec<CustomParticleType> codec = MapCodec.unit(this::getType);

    public CustomParticleType(boolean bl) {
        super(bl);
    }

    @Override
    public MapCodec<CustomParticleType> codec() {
        return codec;
    }

    public CustomParticleType getType() {
        return this;
    }


    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CustomParticleType> streamCodec() {
        return streamCodec;
    }

    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this).toString();
    }
}