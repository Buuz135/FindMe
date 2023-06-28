package com.buuz135.findme.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class CustomParticleType extends ParticleType<CustomParticleType> implements ParticleOptions {
    private static final Deserializer<CustomParticleType> DESERIALIZER = new Deserializer<CustomParticleType>() {
        public CustomParticleType fromCommand(ParticleType<CustomParticleType> particleType, StringReader stringReader) {
            return (CustomParticleType) particleType;
        }

        public CustomParticleType fromNetwork(ParticleType<CustomParticleType> particleType, FriendlyByteBuf friendlyByteBuf) {
            return (CustomParticleType) particleType;
        }
    };
    private final Codec<CustomParticleType> codec = Codec.unit(this::getType);

    public CustomParticleType(boolean bl) {
        super(bl, DESERIALIZER);
    }

    public CustomParticleType getType() {
        return this;
    }

    public Codec<CustomParticleType> codec() {
        return this.codec;
    }

    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this).toString();
    }
}