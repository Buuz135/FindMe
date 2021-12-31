package com.buuz135.findme.mixin;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.client.ParticlePosition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

import static net.minecraft.client.particle.ParticleEngine.RENDER_ORDER;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineAccessor {

    @Shadow
    protected abstract <T extends ParticleOptions> void register(ParticleType<T> particleType, ParticleProvider<T> particleProvider);

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/particle/ParticleEngine;registerProviders()V", cancellable = true)
    private void registerProviders(CallbackInfo ci) {
        this.register(FindMeMod.FINDME.get(), (particleOptions, clientLevel, d, e, f, g, h, i) -> new ParticlePosition(clientLevel, d, e, f, g, h, i));
        if (!RENDER_ORDER.contains(ParticlePosition.CUSTOM)) {
            RENDER_ORDER = new ArrayList<>(RENDER_ORDER);
            RENDER_ORDER.add(ParticlePosition.CUSTOM);
        }
    }
}
