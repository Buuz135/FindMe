package com.buuz135.findme.fabric;

import com.buuz135.findme.FindMeModClient;
import net.fabricmc.api.ClientModInitializer;

public class FindMeClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new FindMeModClient();
    }
}
