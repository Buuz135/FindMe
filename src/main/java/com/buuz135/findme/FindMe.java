package com.buuz135.findme;

import com.buuz135.findme.proxy.ClientProxy;
import com.buuz135.findme.proxy.CommonProxy;
import com.buuz135.findme.proxy.FindMeConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.buuz135.findme.FindMe.MOD_ID;

@Mod(MOD_ID)
public class FindMe {

    public static final String MOD_ID = "findme";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    public static SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "network"), () -> "1.0", (s) -> true, (s) -> true);

    public static CommonProxy proxy;

    public FindMe() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FindMeConfig.COMMON.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FindMeConfig.CLIENT.SPEC);
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(FindMeConfig.COMMON::onConfigReload);
        mod.addListener(FindMeConfig.CLIENT::onConfigReload);
    }

}
