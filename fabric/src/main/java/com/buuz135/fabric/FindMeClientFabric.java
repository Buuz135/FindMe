package com.buuz135.fabric;

import net.fabricmc.api.ClientModInitializer;

public class FindMeClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //ParticleFactoryRegistry.getInstance().register(FindMeMod.FINDME.get(), (particleOptions, clientLevel, d, e, f, g, h, i) -> new ParticlePosition(clientLevel, d, e, f,g, h, i));
    }
}
