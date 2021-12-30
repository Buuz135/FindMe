package com.buuz135.fabric;

import com.buuz135.findme.FindMeMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

public class FindMeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FindMeMod.init();

    }
}
