package com.buuz135.forge;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.FindMeModClient;
import com.buuz135.findme.network.PositionRequestMessage;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.CapabilityItemHandler;

@Mod(FindMeMod.MOD_ID)
public class FindMeModForge {

    public FindMeModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FindMeMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FindMeMod.init();
        FindMeMod.BLOCK_CHECKERS.add((blockEntity, itemStack) -> blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty() && PositionRequestMessage.compareItems(itemStack, handler.getStackInSlot(i))) {
                    return true;
                }
            }
            return false;
        }).orElse(false));
        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> FindMeModClient::new);
    }

}
